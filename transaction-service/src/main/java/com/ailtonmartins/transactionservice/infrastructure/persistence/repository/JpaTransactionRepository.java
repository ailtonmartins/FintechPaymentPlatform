package com.ailtonmartins.transactionservice.infrastructure.persistence.repository;

import com.ailtonmartins.transactionservice.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
