package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;

import java.util.UUID;

public class FindAccountByUserIdUseCase {

    private final AccountRepository accountRepository;

    public FindAccountByUserIdUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResult execute(UUID userId) {
        return accountRepository.findByUserId(userId)
                .map(AccountResult::from)
                .orElseThrow(() -> AccountNotFoundException.byUserId(userId));
    }
}
