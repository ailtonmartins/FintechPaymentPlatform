package com.ailtonmartins.userservice.presentation.dto.response;

import com.ailtonmartins.userservice.application.result.AuthResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt,
        String tokenType
) {

    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(
                result.userId(),
                result.accessToken(),
                result.refreshToken(),
                result.refreshTokenExpiresAt(),
                "Bearer"
        );
    }
}
