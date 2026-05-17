package com.ailtonmartins.userservice.application.command;

public record LoginCommand(
        String email,
        String password
) {
}
