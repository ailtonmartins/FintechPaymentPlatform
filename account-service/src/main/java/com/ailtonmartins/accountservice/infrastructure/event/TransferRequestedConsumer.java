package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.TransferResultPublisher;
import com.ailtonmartins.accountservice.application.usecase.ProcessTransferUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessTransferUseCase processTransferUseCase;
    private final TransferResultPublisher transferResultPublisher;

    public TransferRequestedConsumer(
            ObjectMapper objectMapper,
            ProcessTransferUseCase processTransferUseCase,
            TransferResultPublisher transferResultPublisher
    ) {
        this.objectMapper = objectMapper;
        this.processTransferUseCase = processTransferUseCase;
        this.transferResultPublisher = transferResultPublisher;
    }

    @KafkaListener(topics = "${app.kafka.topics.transfer-requested}")
    public void consume(String payload) throws JsonProcessingException {
        TransferRequestedEvent event = objectMapper.readValue(payload, TransferRequestedEvent.class);
        ProcessTransferCommand command = new ProcessTransferCommand(
                event.transactionId(),
                event.sourceAccountId(),
                event.destinationAccountId(),
                event.amount()
        );

        try {
            processTransferUseCase.execute(command);
            transferResultPublisher.publishCompleted(command);
        } catch (RuntimeException exception) {
            transferResultPublisher.publishFailed(command, exception.getMessage());
        }
    }
}
