package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.RefreshTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para renovacao do access token")
public record RefreshTokenRequest(
        @Schema(description = "Refresh token recebido no login", example = "eyJhbGciOiJIUzI1NiJ9")
        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {

    public RefreshTokenCommand toCommand() {
        return new RefreshTokenCommand(refreshToken);
    }
}
