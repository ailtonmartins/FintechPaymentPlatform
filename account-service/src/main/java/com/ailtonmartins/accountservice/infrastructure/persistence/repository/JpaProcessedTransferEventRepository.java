package com.ailtonmartins.accountservice.infrastructure.persistence.repository;

import com.ailtonmartins.accountservice.infrastructure.persistence.entity.ProcessedTransferEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProcessedTransferEventRepository extends JpaRepository<ProcessedTransferEventEntity, UUID> {
}
