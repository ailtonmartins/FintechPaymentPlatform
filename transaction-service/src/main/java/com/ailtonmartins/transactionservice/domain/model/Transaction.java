package com.ailtonmartins.transactionservice.domain.model;

import com.ailtonmartins.transactionservice.domain.exception.TransactionAlreadyFinishedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private UUID requesterUserId;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transaction(UUID requesterUserId, UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        this(
                UUID.randomUUID(),
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.PENDING,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Transaction(
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
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.requesterUserId = Objects.requireNonNull(requesterUserId, "requesterUserId nao pode ser nulo");
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "sourceAccountId nao pode ser nulo");
        this.destinationAccountId = Objects.requireNonNull(destinationAccountId, "destinationAccountId nao pode ser nulo");
        if (this.sourceAccountId.equals(this.destinationAccountId)) {
            throw new IllegalArgumentException("Conta de origem e destino nao podem ser iguais");
        }
        this.amount = requirePositive(amount, "amount");
        this.type = Objects.requireNonNull(type, "type nao pode ser nulo");
        this.status = Objects.requireNonNull(status, "status nao pode ser nulo");
        this.failureReason = normalizeFailureReason(failureReason);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt nao pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt nao pode ser nulo");
    }

    public UUID getId() {
        return id;
    }

    public UUID getRequesterUserId() {
        return requesterUserId;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }

    public boolean isFinished() {
        return status == TransactionStatus.COMPLETED || status == TransactionStatus.FAILED;
    }

    public void complete() {
        ensurePending();
        this.status = TransactionStatus.COMPLETED;
        this.failureReason = null;
        touch();
    }

    public void fail(String failureReason) {
        ensurePending();
        this.status = TransactionStatus.FAILED;
        this.failureReason = requireText(failureReason, "failureReason");
        touch();
    }

    private void ensurePending() {
        if (!isPending()) {
            throw new TransactionAlreadyFinishedException(id);
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " nao pode ser nulo");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero");
        }
        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nao pode estar em branco");
        }
        return value.trim();
    }

    private static String normalizeFailureReason(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
