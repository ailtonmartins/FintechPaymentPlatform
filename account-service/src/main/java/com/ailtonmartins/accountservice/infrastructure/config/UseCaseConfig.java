package com.ailtonmartins.accountservice.infrastructure.config;

import com.ailtonmartins.accountservice.application.port.AccountNumberGenerator;
import com.ailtonmartins.accountservice.application.usecase.CreateAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.CreditAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.DebitAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByIdUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByUserIdUseCase;
import com.ailtonmartins.accountservice.domain.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateAccountUseCase createAccountUseCase(
            AccountRepository accountRepository,
            AccountNumberGenerator accountNumberGenerator
    ) {
        return new CreateAccountUseCase(accountRepository, accountNumberGenerator);
    }

    @Bean
    public FindAccountByIdUseCase findAccountByIdUseCase(AccountRepository accountRepository) {
        return new FindAccountByIdUseCase(accountRepository);
    }

    @Bean
    public FindAccountByUserIdUseCase findAccountByUserIdUseCase(AccountRepository accountRepository) {
        return new FindAccountByUserIdUseCase(accountRepository);
    }

    @Bean
    public CreditAccountUseCase creditAccountUseCase(AccountRepository accountRepository) {
        return new CreditAccountUseCase(accountRepository);
    }

    @Bean
    public DebitAccountUseCase debitAccountUseCase(AccountRepository accountRepository) {
        return new DebitAccountUseCase(accountRepository);
    }
}
