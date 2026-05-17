package com.ailtonmartins.userservice.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("Ja existe um usuario cadastrado com o e-mail: " + email);
    }
}
