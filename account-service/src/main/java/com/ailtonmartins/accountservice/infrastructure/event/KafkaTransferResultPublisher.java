package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.TransferResultPublisher;
import com.ailtonmartins.accountservice.infrastructure.outbox.OutboxEventWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransferResultPublisher implements TransferResultPublisher {

    private static final String TRANSFER_COMPLETED = "TRANSFER_COMPLETED";
    private static final String TRANSFER_FAILED = "TRANSFER_FAILED";

    private final OutboxEventWriter outboxEventWriter;
    private final ObjectMapper objectMapper;
    private final String transferCompletedTopic;
    private final String transferFailedTopic;

    public KafkaTransferResultPublisher(
            OutboxEventWriter outboxEventWriter,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.transfer-completed}") String transferCompletedTopic,
            @Value("${app.kafka.topics.transfer-failed}") String transferFailedTopic
    ) {
        this.outboxEventWriter = outboxEventWriter;
        this.objectMapper = objectMapper;
        this.transferCompletedTopic = transferCompletedTopic;
        this.transferFailedTopic = transferFailedTopic;
    }

    @Override
    public void publishCompleted(ProcessTransferCommand command) {
        saveOutbox(command, TRANSFER_COMPLETED, transferCompletedTopic, TransferCompletedEvent.from(command));
    }

    @Override
    public void publishFailed(ProcessTransferCommand command, String failureReason) {
        saveOutbox(command, TRANSFER_FAILED, transferFailedTopic, TransferFailedEvent.from(command, failureReason));
    }

    private void saveOutbox(ProcessTransferCommand command, String eventType, String topic, Object event) {
        try {
            outboxEventWriter.save(
                    command.transactionId(),
                    eventType,
                    topic,
                    command.transactionId().toString(),
                    objectMapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Falha ao serializar evento de resultado da transferencia", exception);
        }
    }
}
