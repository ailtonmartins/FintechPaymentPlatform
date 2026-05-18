package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaTransactionEventPublisherTest {

    private static final String TOPIC = "transaction.transfer.requested";

    @Test
    @SuppressWarnings("unchecked")
    void devePublicarEventoDeTransferenciaNoTopicoConfigurado() {
        KafkaTemplate<String, TransferRequestedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTransactionEventPublisher publisher = new KafkaTransactionEventPublisher(kafkaTemplate, TOPIC);
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("150.00")
        );

        SendResult<String, TransferRequestedEvent> sendResult = new SendResult<>(
                new ProducerRecord<>(TOPIC, transaction.getId().toString(), TransferRequestedEvent.from(transaction)),
                new RecordMetadata(new TopicPartition(TOPIC, 0), 0, 1, 0, 0, 0)
        );
        when(kafkaTemplate.send(eq(TOPIC), eq(transaction.getId().toString()), any(TransferRequestedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        publisher.publishTransferRequested(transaction);

        ArgumentCaptor<TransferRequestedEvent> eventCaptor = ArgumentCaptor.forClass(TransferRequestedEvent.class);
        verify(kafkaTemplate).send(eq(TOPIC), eq(transaction.getId().toString()), eventCaptor.capture());

        TransferRequestedEvent event = eventCaptor.getValue();
        assertThat(event.transactionId()).isEqualTo(transaction.getId());
        assertThat(event.requesterUserId()).isEqualTo(transaction.getRequesterUserId());
        assertThat(event.sourceAccountId()).isEqualTo(transaction.getSourceAccountId());
        assertThat(event.destinationAccountId()).isEqualTo(transaction.getDestinationAccountId());
        assertThat(event.amount()).isEqualByComparingTo("150.00");
        assertThat(event.status()).isEqualTo(transaction.getStatus());
        assertThat(event.type()).isEqualTo(transaction.getType());
        assertThat(event.occurredAt()).isNotNull();
    }
}
