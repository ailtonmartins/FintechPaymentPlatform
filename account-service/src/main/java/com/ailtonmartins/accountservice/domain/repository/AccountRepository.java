package com.ailtonmartins.accountservice.domain.repository;

import com.ailtonmartins.accountservice.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByAccountNumber(String accountNumber);
}
