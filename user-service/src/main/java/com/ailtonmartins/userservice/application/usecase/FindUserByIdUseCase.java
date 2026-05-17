package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.exception.UserNotFoundException;
import com.ailtonmartins.userservice.domain.repository.UserRepository;

import java.util.Objects;
import java.util.UUID;

public class FindUserByIdUseCase {

    private final UserRepository userRepository;

    public FindUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository nao pode ser nulo");
    }

    public UserResult execute(UUID id) {
        return userRepository.findById(id)
                .map(UserResult::from)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
