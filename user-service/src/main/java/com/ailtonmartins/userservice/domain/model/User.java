package com.ailtonmartins.userservice.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private Set<Role> roles;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String name, String email, String password) {
        this(UUID.randomUUID(), name, email, password, Set.of(Role.USER), true, LocalDateTime.now(), LocalDateTime.now());
    }

    public User(
            UUID id,
            String name,
            String email,
            String password,
            Set<Role> roles,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.name = requireText(name, "name");
        this.email = requireText(email, "email").toLowerCase();
        this.password = requireText(password, "password");
        this.roles = new HashSet<>(Objects.requireNonNull(roles, "roles nao pode ser nulo"));
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt nao pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt nao pode ser nulo");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateName(String name) {
        this.name = requireText(name, "name");
        touch();
    }

    public void updatePassword(String password) {
        this.password = requireText(password, "password");
        touch();
    }

    public void addRole(Role role) {
        roles.add(Objects.requireNonNull(role, "role nao pode ser nulo"));
        touch();
    }

    public void removeRole(Role role) {
        roles.remove(Objects.requireNonNull(role, "role nao pode ser nulo"));
        touch();
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void activate() {
        this.active = true;
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nao pode estar em branco");
        }
        return value.trim();
    }
}
