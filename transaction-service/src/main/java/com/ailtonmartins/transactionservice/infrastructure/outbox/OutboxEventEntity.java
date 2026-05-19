package com.ailtonmartins.transactionservice.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 150)
    private String topic;

    @Column(name = "message_key", nullable = false, length = 100)
    private String messageKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(UUID aggregateId, String eventType, String topic, String messageKey, String payload) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.status = "PENDING";
        this.attempts = 0;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getPayload() {
        return payload;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStatus() {
        return status;
    }

    public String getLastError() {
        return lastError;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void markPublished() {
        this.status = "PUBLISHED";
        this.publishedAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void markFailure(String error) {
        this.status = "FAILED";
        this.attempts++;
        this.lastError = normalizeError(error);
    }

    public void markPending(String error) {
        this.status = "PENDING";
        this.attempts++;
        this.lastError = normalizeError(error);
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    private static String normalizeError(String error) {
        if (error == null || error.isBlank()) {
            return "Erro ao publicar evento";
        }
        return error.length() > 1000 ? error.substring(0, 1000) : error;
    }
}
