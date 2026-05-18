package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.application.command.FailTransactionCommand;
import com.ailtonmartins.transactionservice.application.usecase.CompleteTransactionUseCase;
import com.ailtonmartins.transactionservice.application.usecase.FailTransactionUseCase;
import com.ailtonmartins.transactionservice.domain.exception.TransactionAlreadyFinishedException;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferResultConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferResultConsumer.class);

    private final ObjectMapper objectMapper;
    private final CompleteTransactionUseCase completeTransactionUseCase;
    private final FailTransactionUseCase failTransactionUseCase;

    public TransferResultConsumer(
            ObjectMapper objectMapper,
            CompleteTransactionUseCase completeTransactionUseCase,
            FailTransactionUseCase failTransactionUseCase
    ) {
        this.objectMapper = objectMapper;
        this.completeTransactionUseCase = completeTransactionUseCase;
        this.failTransactionUseCase = failTransactionUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.transfer-completed}")
    public void consumeCompleted(String payload) throws JsonProcessingException {
        TransferCompletedEvent event = objectMapper.readValue(payload, TransferCompletedEvent.class);
        try {
            completeTransactionUseCase.execute(event.transactionId());
        } catch (TransactionAlreadyFinishedException | TransactionNotFoundException exception) {
            LOGGER.warn("Resultado COMPLETED ignorado para transactionId={}: {}", event.transactionId(), exception.getMessage());
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.transfer-failed}")
    public void consumeFailed(String payload) throws JsonProcessingException {
        TransferFailedEvent event = objectMapper.readValue(payload, TransferFailedEvent.class);
        try {
            failTransactionUseCase.execute(new FailTransactionCommand(event.transactionId(), event.failureReason()));
        } catch (TransactionAlreadyFinishedException | TransactionNotFoundException exception) {
            LOGGER.warn("Resultado FAILED ignorado para transactionId={}: {}", event.transactionId(), exception.getMessage());
        }
    }
}
