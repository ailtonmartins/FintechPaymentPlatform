package com.ailtonmartins.userservice.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciais invalidas");
    }
}
