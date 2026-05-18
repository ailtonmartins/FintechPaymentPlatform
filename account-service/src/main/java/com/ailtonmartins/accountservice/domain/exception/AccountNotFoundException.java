package com.ailtonmartins.accountservice.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID id) {
        super("Conta nao encontrada para o id: " + id);
    }

    public static AccountNotFoundException byUserId(UUID userId) {
        return new AccountNotFoundException("Conta nao encontrada para o usuario: " + userId);
    }

    private AccountNotFoundException(String message) {
        super(message);
    }
}
