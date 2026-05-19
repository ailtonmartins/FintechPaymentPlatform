package com.ailtonmartins.transactionservice.infrastructure.event;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.infrastructure.outbox.OutboxEventWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaTransactionEventPublisherTest {

    private static final String TOPIC = "transaction.transfer.requested";

    @Test
    void deveSalvarEventoDeTransferenciaNaOutbox() {
        OutboxEventWriter outboxEventWriter = mock(OutboxEventWriter.class);
        KafkaTransactionEventPublisher publisher = new KafkaTransactionEventPublisher(
                outboxEventWriter,
                new ObjectMapper().findAndRegisterModules(),
                TOPIC
        );
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("150.00")
        );

        publisher.publishTransferRequested(transaction);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(outboxEventWriter).save(
                eq(transaction.getId()),
                eq("TRANSFER_REQUESTED"),
                eq(TOPIC),
                eq(transaction.getId().toString()),
                payloadCaptor.capture()
        );

        assertThat(payloadCaptor.getValue()).contains(transaction.getId().toString());
        assertThat(payloadCaptor.getValue()).contains(transaction.getSourceAccountId().toString());
        assertThat(payloadCaptor.getValue()).contains(transaction.getDestinationAccountId().toString());
    }
}
