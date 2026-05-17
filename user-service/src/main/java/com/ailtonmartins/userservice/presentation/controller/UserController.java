package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByEmailUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.presentation.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindUserByEmailUseCase findUserByEmailUseCase;

    public UserController(
            FindUserByIdUseCase findUserByIdUseCase,
            FindUserByEmailUseCase findUserByEmailUseCase
    ) {
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findUserByEmailUseCase = findUserByEmailUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        UserResult result = findUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @GetMapping
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email) {
        UserResult result = findUserByEmailUseCase.execute(email);
        return ResponseEntity.ok(UserResponse.from(result));
    }
}
