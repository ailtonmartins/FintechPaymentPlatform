package com.ailtonmartins.accountservice.application.usecase;

import com.ailtonmartins.accountservice.application.command.ProcessTransferCommand;
import com.ailtonmartins.accountservice.application.port.ProcessedTransferEventRepository;
import com.ailtonmartins.accountservice.application.port.TransferResultPublisher;
import com.ailtonmartins.accountservice.application.result.ProcessTransferResult;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.exception.InactiveAccountException;
import com.ailtonmartins.accountservice.domain.exception.InsufficientBalanceException;
import com.ailtonmartins.accountservice.domain.model.Account;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;

public class ProcessTransferUseCase {

    private final AccountRepository accountRepository;
    private final ProcessedTransferEventRepository processedTransferEventRepository;
    private final TransferResultPublisher transferResultPublisher;

    public ProcessTransferUseCase(
            AccountRepository accountRepository,
            ProcessedTransferEventRepository processedTransferEventRepository,
            TransferResultPublisher transferResultPublisher
    ) {
        this.accountRepository = accountRepository;
        this.processedTransferEventRepository = processedTransferEventRepository;
        this.transferResultPublisher = transferResultPublisher;
    }

    @Transactional
    public ProcessTransferResult execute(ProcessTransferCommand command) {
        ProcessTransferResult result = processedTransferEventRepository.findByTransactionId(command.transactionId())
                .map(this::alreadyProcessed)
                .orElseGet(() -> process(command));
        publishResult(result);
        return result;
    }

    private ProcessTransferResult process(ProcessTransferCommand command) {
        try {
            Account sourceAccount = accountRepository.findById(command.sourceAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(command.sourceAccountId()));
            Account destinationAccount = accountRepository.findById(command.destinationAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(command.destinationAccountId()));

            sourceAccount.debit(command.amount());
            destinationAccount.credit(command.amount());

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            return processedTransferEventRepository.saveCompleted(command);
        } catch (AccountNotFoundException | InactiveAccountException | InsufficientBalanceException exception) {
            return processedTransferEventRepository.saveFailed(command, exception.getMessage());
        }
    }

    private ProcessTransferResult alreadyProcessed(ProcessTransferResult result) {
        return result.completed()
                ? ProcessTransferResult.completed(result.command(), true)
                : ProcessTransferResult.failed(result.command(), result.failureReason(), true);
    }

    private void publishResult(ProcessTransferResult result) {
        if (result.alreadyProcessed()) {
            return;
        }
        if (result.completed()) {
            transferResultPublisher.publishCompleted(result.command());
            return;
        }
        transferResultPublisher.publishFailed(result.command(), result.failureReason());
    }
}
