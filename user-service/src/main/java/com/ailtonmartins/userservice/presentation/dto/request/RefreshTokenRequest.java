package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.RefreshTokenCommand;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {

    public RefreshTokenCommand toCommand() {
        return new RefreshTokenCommand(refreshToken);
    }
}
