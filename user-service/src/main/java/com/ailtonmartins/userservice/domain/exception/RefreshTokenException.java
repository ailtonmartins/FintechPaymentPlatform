package com.ailtonmartins.userservice.domain.exception;

public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String message) {
        super(message);
    }

    public static RefreshTokenException invalid() {
        return new RefreshTokenException("Refresh token invalido");
    }

    public static RefreshTokenException expired() {
        return new RefreshTokenException("Refresh token expirado");
    }

    public static RefreshTokenException revoked() {
        return new RefreshTokenException("Refresh token revogado");
    }
}
