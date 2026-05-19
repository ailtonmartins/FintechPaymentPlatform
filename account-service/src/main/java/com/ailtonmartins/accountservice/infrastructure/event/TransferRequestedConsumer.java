package com.ailtonmartins.accountservice.infrastructure.event;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.result.ProcessTransferResult;
import com.ailtonmartins.accountservice.application.usecase.ProcessTransferUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferRequestedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRequestedConsumer.class);

    private final ObjectMapper objectMapper;
    private final ProcessTransferUseCase processTransferUseCase;

    public TransferRequestedConsumer(
            ObjectMapper objectMapper,
            ProcessTransferUseCase processTransferUseCase
    ) {
        this.objectMapper = objectMapper;
        this.processTransferUseCase = processTransferUseCase;
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

        ProcessTransferResult result = processTransferUseCase.execute(command);
        if (result.alreadyProcessed()) {
            LOGGER.info("Transferencia ja processada, republicando resultado transactionId={}", command.transactionId());
        }

    }
}
