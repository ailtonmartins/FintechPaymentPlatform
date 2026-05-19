package com.ailtonmartins.accountservice.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findTop10ByStatusOrderByCreatedAtAsc(String status);
}
