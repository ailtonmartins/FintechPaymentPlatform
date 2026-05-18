package com.ailtonmartins.transactionservice.infrastructure.persistence.adapter;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.infrastructure.persistence.repository.JpaTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TransactionRepositoryAdapter.class)
class TransactionRepositoryAdapterTest {

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @Test
    void deveSalvarTransacao() {
        Transaction transaction = transaction();

        Transaction savedTransaction = transactionRepositoryAdapter.save(transaction);

        assertThat(savedTransaction.getId()).isEqualTo(transaction.getId());
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(jpaTransactionRepository.findById(transaction.getId())).isPresent();
    }

    @Test
    void deveBuscarTransacaoPorId() {
        Transaction savedTransaction = transactionRepositoryAdapter.save(transaction());

        Optional<Transaction> result = transactionRepositoryAdapter.findById(savedTransaction.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTransaction.getId());
        assertThat(result.get().getAmount()).isEqualByComparingTo("100.00");
    }

    private static Transaction transaction() {
        return new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );
    }
}
