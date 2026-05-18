package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingTransactionEventPublisher implements TransactionEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTransactionEventPublisher.class);

    @Override
    public void publishTransferRequested(Transaction transaction) {
        LOGGER.info(
                "Evento TRANSFER_REQUESTED pendente de integracao Kafka para transactionId={}",
                transaction.getId()
        );
    }
}
