package com.ailtonmartins.userservice.application.result;

import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResult(
        UUID id,
        String name,
        String email,
        Set<Role> roles,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
