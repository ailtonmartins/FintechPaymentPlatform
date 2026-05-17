package com.ailtonmartins.userservice.application.command;

public record RegisterUserCommand(
        String name,
        String email,
        String password
) {
}
