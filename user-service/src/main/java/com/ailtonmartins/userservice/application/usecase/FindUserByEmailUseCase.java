package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.exception.UserNotFoundException;
import com.ailtonmartins.userservice.domain.repository.UserRepository;

import java.util.Objects;

public class FindUserByEmailUseCase {

    private final UserRepository userRepository;

    public FindUserByEmailUseCase(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository nao pode ser nulo");
    }

    public UserResult execute(String email) {
        return userRepository.findByEmail(email)
                .map(UserResult::from)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
