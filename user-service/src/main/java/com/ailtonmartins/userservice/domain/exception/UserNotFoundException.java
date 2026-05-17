package com.ailtonmartins.userservice.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("Usuario nao encontrado para o id: " + userId);
    }

    public UserNotFoundException(String email) {
        super("Usuario nao encontrado para o e-mail: " + email);
    }
}
