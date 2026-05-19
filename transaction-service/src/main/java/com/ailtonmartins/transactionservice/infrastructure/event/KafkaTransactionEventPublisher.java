package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.infrastructure.outbox.OutboxEventWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

    private static final String TRANSFER_REQUESTED = "TRANSFER_REQUESTED";

    private final OutboxEventWriter outboxEventWriter;
    private final ObjectMapper objectMapper;
    private final String transferRequestedTopic;

    public KafkaTransactionEventPublisher(
            OutboxEventWriter outboxEventWriter,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.transfer-requested}") String transferRequestedTopic
    ) {
        this.outboxEventWriter = outboxEventWriter;
        this.objectMapper = objectMapper;
        this.transferRequestedTopic = transferRequestedTopic;
    }

    @Override
    public void publishTransferRequested(Transaction transaction) {
        TransferRequestedEvent event = TransferRequestedEvent.from(transaction);
        String key = transaction.getId().toString();

        try {
            outboxEventWriter.save(
                    transaction.getId(),
                    TRANSFER_REQUESTED,
                    transferRequestedTopic,
                    key,
                    objectMapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Falha ao serializar evento de transferencia", exception);
        }
    }
}
