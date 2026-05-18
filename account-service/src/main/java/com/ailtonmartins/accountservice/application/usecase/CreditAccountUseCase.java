package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.AccountOperationCommand;
import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;

public class CreditAccountUseCase {

    private final AccountRepository accountRepository;

    public CreditAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResult execute(AccountOperationCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        account.credit(command.amount());

        return AccountResult.from(accountRepository.save(account));
    }
}
