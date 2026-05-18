package com.ailtonmartins.accountservice.domain.exception;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException() {
        super("Conta inativa");
    }
}
