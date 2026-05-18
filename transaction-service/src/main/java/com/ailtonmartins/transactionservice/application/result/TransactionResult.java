package com.ailtonmartins.transactionservice.application.result;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResult(
        UUID id,
        UUID requesterUserId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TransactionResult from(Transaction transaction) {
        return new TransactionResult(
                transaction.getId(),
                transaction.getRequesterUserId(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getFailureReason(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
