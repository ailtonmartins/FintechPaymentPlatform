package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.CreateAccountCommand;
import com.ailtonmartins.accountservice.application.port.AccountNumberGenerator;
import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.domain.exception.AccountAlreadyExistsException;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;

public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public CreateAccountUseCase(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    public AccountResult execute(CreateAccountCommand command) {
        if (accountRepository.existsByUserId(command.userId())) {
            throw new AccountAlreadyExistsException(command.userId());
        }

        String accountNumber = generateUniqueAccountNumber();
        Account account = new Account(command.userId(), accountNumber);

        return AccountResult.from(accountRepository.save(account));
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
