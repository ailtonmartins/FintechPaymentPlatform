package com.ailtonmartins.apigateway.infrastructure.security;

import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHENTICATED_USER_ID_HEADER = "X-Authenticated-User-Id";
    private static final String AUTHENTICATED_USER_EMAIL_HEADER = "X-Authenticated-User-Email";

    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/api/v1/auth/",
            "/swagger-ui/",
            "/webjars/swagger-ui/",
            "/v3/api-docs",
            "/user-service/v3/api-docs",
            "/account-service/v3/api-docs",
            "/transaction-service/v3/api-docs"
    );

    private final JwtAccessTokenProvider jwtAccessTokenProvider;

    public JwtAuthenticationGatewayFilter(JwtAccessTokenProvider jwtAccessTokenProvider) {
        this.jwtAccessTokenProvider = jwtAccessTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        if (token == null) {
            return unauthorized(exchange);
        }

        try {
            JwtAccessTokenProvider.JwtUserClaims claims = jwtAccessTokenProvider.validateAndExtract(token);
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        headers.remove(AUTHENTICATED_USER_ID_HEADER);
                        headers.remove(AUTHENTICATED_USER_EMAIL_HEADER);
                        headers.add(AUTHENTICATED_USER_ID_HEADER, claims.userId().toString());
                        if (claims.email() != null) {
                            headers.add(AUTHENTICATED_USER_EMAIL_HEADER, claims.email());
                        }
                    })
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtException | IllegalArgumentException exception) {
            return unauthorized(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private static boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private static String extractToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length());
    }

    private static Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
