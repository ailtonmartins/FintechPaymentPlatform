package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.command.RegisterUserCommand;
import com.ailtonmartins.userservice.application.port.PasswordHasher;
import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.exception.UserAlreadyExistsException;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.UserRepository;

import java.util.Objects;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository nao pode ser nulo");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher nao pode ser nulo");
    }

    public UserResult execute(RegisterUserCommand command) {
        Objects.requireNonNull(command, "command nao pode ser nulo");

        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String hashedPassword = passwordHasher.hash(command.password());
        User user = new User(command.name(), command.email(), hashedPassword);

        return UserResult.from(userRepository.save(user));
    }
}
