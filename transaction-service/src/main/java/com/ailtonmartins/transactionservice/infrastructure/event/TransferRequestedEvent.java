package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferRequestedEvent(
        UUID transactionId,
        UUID requesterUserId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        LocalDateTime occurredAt
) {

    public static TransferRequestedEvent from(Transaction transaction) {
        return new TransferRequestedEvent(
                transaction.getId(),
                transaction.getRequesterUserId(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                LocalDateTime.now()
        );
    }
}
