package com.ailtonmartins.accountservice.infrastructure.persistence.mapper;

import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.infrastructure.persistence.entity.AccountEntity;

public class AccountPersistenceMapper {

    private AccountPersistenceMapper() {
    }

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public static Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getUserId(),
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
