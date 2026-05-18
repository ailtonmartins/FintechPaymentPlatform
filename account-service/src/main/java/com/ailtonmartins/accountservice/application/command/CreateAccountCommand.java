package com.ailtonmartins.accountservice.application.command;

import java.util.UUID;

public record CreateAccountCommand(UUID userId) {
}
