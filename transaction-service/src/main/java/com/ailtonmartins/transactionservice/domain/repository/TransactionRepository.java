package com.ailtonmartins.transactionservice.domain.repository;

import com.ailtonmartins.transactionservice.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
