package com.ailtonmartins.userservice.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher passwordHasher = new BCryptPasswordHasher(new BCryptPasswordEncoder());

    @Test
    void deveCriptografarSenhaEValidarSenhaOriginal() {
        String hashedPassword = passwordHasher.hash("123456");

        assertThat(hashedPassword).isNotEqualTo("123456");
        assertThat(passwordHasher.matches("123456", hashedPassword)).isTrue();
        assertThat(passwordHasher.matches("senha-errada", hashedPassword)).isFalse();
    }
}
