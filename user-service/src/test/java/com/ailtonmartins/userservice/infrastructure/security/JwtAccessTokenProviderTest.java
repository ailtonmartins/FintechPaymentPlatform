package com.ailtonmartins.userservice.infrastructure.security;

import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAccessTokenProviderTest {

    @Test
    void deveGerarJwtCompacto() {
        JwtAccessTokenProvider accessTokenProvider = new JwtAccessTokenProvider(
                "segredo-de-teste",
                900000L
        );
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        user.addRole(Role.ADMIN);

        String token = accessTokenProvider.generate(user);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void deveGerarTokensDiferentesParaUsuariosDiferentes() {
        JwtAccessTokenProvider accessTokenProvider = new JwtAccessTokenProvider(
                "segredo-de-teste",
                900000L
        );
        User firstUser = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        User secondUser = new User(
                "Admin",
                "admin@email.com",
                "senha-criptografada"
        );

        String firstToken = accessTokenProvider.generate(firstUser);
        String secondToken = accessTokenProvider.generate(secondUser);

        assertThat(Set.of(firstToken, secondToken)).hasSize(2);
    }

    @Test
    void deveValidarTokenEExtrairClaims() {
        JwtAccessTokenProvider accessTokenProvider = new JwtAccessTokenProvider(
                "segredo-de-teste",
                900000L
        );
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        user.addRole(Role.ADMIN);

        String token = accessTokenProvider.generate(user);

        JwtAccessTokenProvider.JwtUserClaims claims = accessTokenProvider.validateAndExtract(token);

        assertThat(claims.userId()).isEqualTo(user.getId());
        assertThat(claims.email()).isEqualTo("ailton@email.com");
        assertThat(claims.roles()).containsExactlyInAnyOrder("USER", "ADMIN");
    }
}
