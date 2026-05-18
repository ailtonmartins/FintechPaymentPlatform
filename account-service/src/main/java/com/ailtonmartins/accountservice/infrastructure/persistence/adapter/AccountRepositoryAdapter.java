package com.ailtonmartins.accountservice.infrastructure.persistence.adapter;

import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import com.ailtonmartins.accountservice.infrastructure.persistence.mapper.AccountPersistenceMapper;
import com.ailtonmartins.accountservice.infrastructure.persistence.repository.JpaAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    public AccountRepositoryAdapter(JpaAccountRepository jpaAccountRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }

    @Override
    public Account save(Account account) {
        return AccountPersistenceMapper.toDomain(
                jpaAccountRepository.save(AccountPersistenceMapper.toEntity(account))
        );
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaAccountRepository.findById(id)
                .map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUserId(UUID userId) {
        return jpaAccountRepository.findByUserId(userId)
                .map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaAccountRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaAccountRepository.existsByAccountNumber(accountNumber);
    }
}
