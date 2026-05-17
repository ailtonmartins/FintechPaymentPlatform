package com.ailtonmartins.userservice.infrastructure.persistence.mapper;

import com.ailtonmartins.userservice.domain.model.RefreshToken;
import com.ailtonmartins.userservice.infrastructure.persistence.entity.RefreshTokenEntity;

public class RefreshTokenPersistenceMapper {

    private RefreshTokenPersistenceMapper() {
    }

    public static RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenEntity(
                refreshToken.getId(),
                refreshToken.getUserId(),
                refreshToken.getToken(),
                refreshToken.getExpiresAt(),
                refreshToken.isRevoked(),
                refreshToken.getCreatedAt()
        );
    }

    public static RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                null
        );
    }
}
