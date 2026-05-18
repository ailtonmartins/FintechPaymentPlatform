package com.ailtonmartins.transactionservice.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(UUID id) {
        super("Transacao nao encontrada para o id: " + id);
    }
}
