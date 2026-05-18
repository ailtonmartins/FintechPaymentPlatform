package com.ailtonmartins.accountservice.domain.exception;

import java.util.UUID;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(UUID userId) {
        super("Ja existe uma conta cadastrada para o usuario: " + userId);
    }
}
