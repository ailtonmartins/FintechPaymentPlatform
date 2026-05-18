package com.ailtonmartins.transactionservice.infrastructure.persistence.adapter;

import com.ailtonmartins.transactionservice.domain.model.Transaction;
import com.ailtonmartins.transactionservice.domain.repository.TransactionRepository;
import com.ailtonmartins.transactionservice.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import com.ailtonmartins.transactionservice.infrastructure.persistence.repository.JpaTransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepository jpaTransactionRepository;

    public TransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository) {
        this.jpaTransactionRepository = jpaTransactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return TransactionPersistenceMapper.toDomain(
                jpaTransactionRepository.save(TransactionPersistenceMapper.toEntity(transaction))
        );
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaTransactionRepository.findById(id)
                .map(TransactionPersistenceMapper::toDomain);
    }
}
