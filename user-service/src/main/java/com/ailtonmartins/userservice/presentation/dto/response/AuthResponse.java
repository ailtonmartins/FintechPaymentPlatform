package com.ailtonmartins.userservice.presentation.dto.response;

import com.ailtonmartins.userservice.application.result.AuthResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta de autenticacao")
public record AuthResponse(
        @Schema(description = "Identificador do usuario autenticado", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "Access token JWT")
        String accessToken,

        @Schema(description = "Refresh token opaco")
        String refreshToken,

        @Schema(description = "Data e hora de expiracao do refresh token")
        LocalDateTime refreshTokenExpiresAt,

        @Schema(description = "Tipo do token de acesso", example = "Bearer")
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
