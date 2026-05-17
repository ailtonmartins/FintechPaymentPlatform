package com.ailtonmartins.userservice.presentation.dto.response;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.model.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Set<Role> roles,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.name(),
                result.email(),
                result.roles(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
