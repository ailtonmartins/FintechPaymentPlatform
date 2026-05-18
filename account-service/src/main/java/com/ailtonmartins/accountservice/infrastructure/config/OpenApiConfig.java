package com.ailtonmartins.accountservice.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI accountServiceOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o JWT sem o prefixo Bearer")))
                .info(new Info()
                        .title("Account Service API")
                        .description("API de contas e saldo da Fintech Payment Platform")
                        .version("v1")
                        .contact(new Contact()
                                .name("Ailton Martins")
                                .url("https://github.com/ailtonmartins"))
                        .license(new License()
                                .name("Uso educacional")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Ambiente local")
                ));
    }
}
