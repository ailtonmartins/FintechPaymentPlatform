package com.ailtonmartins.userservice.infrastructure.persistence.mapper;

import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.infrastructure.persistence.entity.RefreshTokenEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenPersistenceMapperTest {

    @Test
    void deveConverterDominioParaEntidade() {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "refresh-token",
                LocalDateTime.now().plusDays(7),
                false,
                LocalDateTime.now().minusMinutes(5),
                null
        );

        RefreshTokenEntity entity = RefreshTokenPersistenceMapper.toEntity(refreshToken);

        assertThat(entity.getId()).isEqualTo(refreshToken.getId());
        assertThat(entity.getUserId()).isEqualTo(refreshToken.getUserId());
        assertThat(entity.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(entity.getExpiresAt()).isEqualTo(refreshToken.getExpiresAt());
        assertThat(entity.isRevoked()).isFalse();
        assertThat(entity.getCreatedAt()).isEqualTo(refreshToken.getCreatedAt());
    }

    @Test
    void deveConverterEntidadeParaDominio() {
        RefreshTokenEntity entity = new RefreshTokenEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "refresh-token",
                LocalDateTime.now().plusDays(7),
                false,
                LocalDateTime.now().minusMinutes(5)
        );

        RefreshToken refreshToken = RefreshTokenPersistenceMapper.toDomain(entity);

        assertThat(refreshToken.getId()).isEqualTo(entity.getId());
        assertThat(refreshToken.getUserId()).isEqualTo(entity.getUserId());
        assertThat(refreshToken.getToken()).isEqualTo(entity.getToken());
        assertThat(refreshToken.getExpiresAt()).isEqualTo(entity.getExpiresAt());
        assertThat(refreshToken.isRevoked()).isFalse();
        assertThat(refreshToken.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}
