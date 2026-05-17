package com.ailtonmartins.userservice.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecureRefreshTokenGeneratorTest {

    private final SecureRefreshTokenGenerator refreshTokenGenerator = new SecureRefreshTokenGenerator();

    @Test
    void deveGerarTokensNaoVaziosEDiferentes() {
        String firstToken = refreshTokenGenerator.generate();
        String secondToken = refreshTokenGenerator.generate();

        assertThat(firstToken).isNotBlank();
        assertThat(secondToken).isNotBlank();
        assertThat(firstToken).isNotEqualTo(secondToken);
    }
}
