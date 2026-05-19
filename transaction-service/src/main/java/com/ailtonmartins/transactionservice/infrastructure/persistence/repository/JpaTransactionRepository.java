package com.ailtonmartins.transactionservice.infrastructure.persistence.repository;

import com.ailtonmartins.transactionservice.infrastructure.persistence.entity.TransactionEntity;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    long countByStatus(TransactionStatus status);

    List<TransactionEntity> findTop20ByStatusOrderByCreatedAtAsc(TransactionStatus status);
}
