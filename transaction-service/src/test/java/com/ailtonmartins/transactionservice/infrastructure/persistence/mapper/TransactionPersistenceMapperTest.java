package com.ailtonmartins.transactionservice.infrastructure.persistence.mapper;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.model.TransactionType;
import com.ailtonmartins.transactionservice.infrastructure.persistence.entity.TransactionEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionPersistenceMapperTest {

    @Test
    void deveConverterDominioParaEntidade() {
        Transaction transaction = transaction();

        TransactionEntity entity = TransactionPersistenceMapper.toEntity(transaction);

        assertThat(entity.getId()).isEqualTo(transaction.getId());
        assertThat(entity.getRequesterUserId()).isEqualTo(transaction.getRequesterUserId());
        assertThat(entity.getSourceAccountId()).isEqualTo(transaction.getSourceAccountId());
        assertThat(entity.getDestinationAccountId()).isEqualTo(transaction.getDestinationAccountId());
        assertThat(entity.getAmount()).isEqualByComparingTo(transaction.getAmount());
        assertThat(entity.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(entity.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(entity.getFailureReason()).isNull();
    }

    @Test
    void deveConverterEntidadeParaDominio() {
        Transaction transaction = transaction();
        TransactionEntity entity = TransactionPersistenceMapper.toEntity(transaction);

        Transaction domain = TransactionPersistenceMapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getRequesterUserId()).isEqualTo(entity.getRequesterUserId());
        assertThat(domain.getSourceAccountId()).isEqualTo(entity.getSourceAccountId());
        assertThat(domain.getDestinationAccountId()).isEqualTo(entity.getDestinationAccountId());
        assertThat(domain.getAmount()).isEqualByComparingTo(entity.getAmount());
        assertThat(domain.getType()).isEqualTo(entity.getType());
        assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
    }

    private static Transaction transaction() {
        return new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                TransactionType.TRANSFER,
                TransactionStatus.PENDING,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
