package com.ailtonmartins.accountservice.infrastructure.persistence.repository;

import com.ailtonmartins.accountservice.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByAccountNumber(String accountNumber);
}
