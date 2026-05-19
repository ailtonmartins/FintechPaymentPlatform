package com.ailtonmartins.accountservice.presentation.dto.response;

import com.ailtonmartins.accountservice.infrastructure.outbox.OutboxEventEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record OutboxEventResponse(
        UUID id,
        String eventType,
        String topic,
        String messageKey,
        String status,
        int attempts,
        String lastError,
        LocalDateTime createdAt
) {

    public static OutboxEventResponse from(OutboxEventEntity event) {
        return new OutboxEventResponse(
                event.getId(),
                event.getEventType(),
                event.getTopic(),
                event.getMessageKey(),
                event.getStatus(),
                event.getAttempts(),
                event.getLastError(),
                event.getCreatedAt()
        );
    }
}
