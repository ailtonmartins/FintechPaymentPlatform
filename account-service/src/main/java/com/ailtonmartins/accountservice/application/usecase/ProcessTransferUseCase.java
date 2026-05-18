package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;

public class ProcessTransferUseCase {

    private final AccountRepository accountRepository;

    public ProcessTransferUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void execute(ProcessTransferCommand command) {
        Account sourceAccount = accountRepository.findById(command.sourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem nao encontrada"));
        Account destinationAccount = accountRepository.findById(command.destinationAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino nao encontrada"));

        sourceAccount.debit(command.amount());
        destinationAccount.credit(command.amount());

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
}
