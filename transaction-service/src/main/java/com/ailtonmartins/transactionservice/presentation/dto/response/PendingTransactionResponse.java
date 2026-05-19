package com.ailtonmartins.transactionservice.presentation.dto.response;

import com.ailtonmartins.transactionservice.infrastructure.persistence.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PendingTransactionResponse(
        UUID id,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        LocalDateTime createdAt
) {

    public static PendingTransactionResponse from(TransactionEntity transaction) {
        return new PendingTransactionResponse(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getCreatedAt()
        );
    }
}
