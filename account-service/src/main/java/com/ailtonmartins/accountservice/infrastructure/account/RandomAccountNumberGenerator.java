package com.ailtonmartins.accountservice.infrastructure.account;

import com.ailtonmartins.accountservice.application.port.AccountNumberGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomAccountNumberGenerator implements AccountNumberGenerator {

    private static final int MIN_ACCOUNT_NUMBER = 10_000_000;
    private static final int ACCOUNT_NUMBER_RANGE = 90_000_000;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        int number = MIN_ACCOUNT_NUMBER + secureRandom.nextInt(ACCOUNT_NUMBER_RANGE);
        return String.valueOf(number);
    }
}
