package com.ailtonmartins.accountservice.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final JpaOutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int maxAttempts;

    public OutboxEventPublisher(
            JpaOutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.outbox.max-attempts}") int maxAttempts
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.maxAttempts = maxAttempts;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms}")
    @Transactional
    public void publishPendingEvents() {
        repository.findTop10ByStatusOrderByCreatedAtAsc("PENDING")
                .forEach(this::publish);
    }

    private void publish(OutboxEventEntity event) {
        try {
            kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload()).get();
            event.markPublished();
            repository.save(event);
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.error("Falha ao publicar outboxEventId={} topic={}", event.getId(), event.getTopic(), exception);
            if (event.getAttempts() + 1 >= maxAttempts) {
                event.markFailure(exception.getMessage());
            } else {
                event.markPending(exception.getMessage());
            }
            repository.save(event);
        }
    }
}
