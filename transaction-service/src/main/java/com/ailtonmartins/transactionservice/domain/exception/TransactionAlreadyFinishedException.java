package com.ailtonmartins.transactionservice.domain.exception;

import java.util.UUID;

public class TransactionAlreadyFinishedException extends RuntimeException {

    public TransactionAlreadyFinishedException(UUID transactionId) {
        super("Transacao ja finalizada para o id: " + transactionId);
    }
}
