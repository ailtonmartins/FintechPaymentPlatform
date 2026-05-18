package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.application.port.TransactionEventPublisher;
import com.ailtonmartins.transactionservice.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTransactionEventPublisher.class);

    private final KafkaTemplate<String, TransferRequestedEvent> kafkaTemplate;
    private final String transferRequestedTopic;

    public KafkaTransactionEventPublisher(
            KafkaTemplate<String, TransferRequestedEvent> kafkaTemplate,
            @Value("${app.kafka.topics.transfer-requested}") String transferRequestedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.transferRequestedTopic = transferRequestedTopic;
    }

    @Override
    public void publishTransferRequested(Transaction transaction) {
        TransferRequestedEvent event = TransferRequestedEvent.from(transaction);
        String key = transaction.getId().toString();

        kafkaTemplate.send(transferRequestedTopic, key, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        LOGGER.error(
                                "Falha ao publicar evento de transferencia transactionId={} topic={}",
                                transaction.getId(),
                                transferRequestedTopic,
                                exception
                        );
                        return;
                    }

                    LOGGER.info(
                            "Evento de transferencia publicado transactionId={} topic={} partition={} offset={}",
                            transaction.getId(),
                            transferRequestedTopic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}
