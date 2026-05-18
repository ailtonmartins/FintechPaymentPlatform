package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;

import java.util.UUID;

public class FindAccountByIdUseCase {

    private final AccountRepository accountRepository;

    public FindAccountByIdUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResult execute(UUID id) {
        return accountRepository.findById(id)
                .map(AccountResult::from)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
