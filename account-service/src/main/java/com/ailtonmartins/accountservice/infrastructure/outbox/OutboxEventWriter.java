package com.ailtonmartins.accountservice.infrastructure.outbox;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OutboxEventWriter {

    private final JpaOutboxEventRepository repository;

    public OutboxEventWriter(JpaOutboxEventRepository repository) {
        this.repository = repository;
    }

    public void save(UUID aggregateId, String eventType, String topic, String messageKey, String payload) {
        repository.save(new OutboxEventEntity(aggregateId, eventType, topic, messageKey, payload));
    }
}
