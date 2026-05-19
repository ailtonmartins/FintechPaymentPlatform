package com.ailtonmartins.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_transfer_events")
public class ProcessedTransferEventEntity {

    @Id
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;

    @Column(name = "destination_account_id", nullable = false)
    private UUID destinationAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    protected ProcessedTransferEventEntity() {
    }

    public ProcessedTransferEventEntity(
            UUID transactionId,
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount,
            String status,
            String failureReason,
            LocalDateTime processedAt
    ) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
    }

    public UUID getTransactionId() {
        return transactionId;
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

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    @PrePersist
    private void prePersist() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}
