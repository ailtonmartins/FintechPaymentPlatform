package com.ailtonmartins.apigateway.infrastructure.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OpenApiServerRewriteGatewayFilter implements GlobalFilter, Ordered {

    private static final List<String> OPENAPI_PATHS = List.of(
            "/user-service/v3/api-docs",
            "/account-service/v3/api-docs",
            "/transaction-service/v3/api-docs"
    );

    private final ObjectMapper objectMapper;

    public OpenApiServerRewriteGatewayFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!OPENAPI_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (!(body instanceof Flux<? extends DataBuffer> fluxBody)) {
                    return super.writeWith(body);
                }

                return DataBufferUtils.join(fluxBody)
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            byte[] responseBytes = rewriteServers(bytes, gatewayBaseUrl(exchange));
                            getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            getHeaders().setContentLength(responseBytes.length);
                            getHeaders().remove(HttpHeaders.TRANSFER_ENCODING);

                            return super.writeWith(Mono.just(bufferFactory.wrap(responseBytes)));
                        });
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -2;
    }

    private byte[] rewriteServers(byte[] responseBody, String gatewayBaseUrl) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root instanceof ObjectNode objectNode) {
                ArrayNode servers = objectMapper.createArrayNode();
                servers.add(objectMapper.createObjectNode()
                        .put("url", gatewayBaseUrl)
                        .put("description", "API Gateway local"));
                objectNode.set("servers", servers);
                return objectMapper.writeValueAsBytes(objectNode);
            }
        } catch (Exception exception) {
            return responseBody;
        }

        return responseBody;
    }

    private static String gatewayBaseUrl(ServerWebExchange exchange) {
        String scheme = exchange.getRequest().getURI().getScheme();
        String host = exchange.getRequest().getHeaders().getFirst(HttpHeaders.HOST);
        return scheme + "://" + host;
    }
}
