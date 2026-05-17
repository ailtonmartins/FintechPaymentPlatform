package com.ailtonmartins.userservice.application.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthResult(
        UUID userId,
        String accessToken,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt
) {
}
