package com.ailtonmartins.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=segredo-de-teste")
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
