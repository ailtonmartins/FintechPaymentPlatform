package com.ailtonmartins.userservice.infrastructure.persistence.mapper;

import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceMapperTest {

    @Test
    void deveConverterDominioParaEntidadeComMultiplasRoles() {
        User user = new User(
                UUID.randomUUID(),
                "Ailton Martins",
                "ailton@email.com",
                "senha-criptografada",
                Set.of(Role.USER, Role.ADMIN),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        UserEntity entity = UserPersistenceMapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(user.getId());
        assertThat(entity.getName()).isEqualTo(user.getName());
        assertThat(entity.getEmail()).isEqualTo(user.getEmail());
        assertThat(entity.getPassword()).isEqualTo(user.getPassword());
        assertThat(entity.getRoles()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
        assertThat(entity.isEnabled()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    void deveConverterEntidadeParaDominioComMultiplasRoles() {
        UserEntity entity = new UserEntity(
                UUID.randomUUID(),
                "Ailton Martins",
                "ailton@email.com",
                "senha-criptografada",
                Set.of(Role.USER, Role.ADMIN),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        User user = UserPersistenceMapper.toDomain(entity);

        assertThat(user.getId()).isEqualTo(entity.getId());
        assertThat(user.getName()).isEqualTo(entity.getName());
        assertThat(user.getEmail()).isEqualTo(entity.getEmail());
        assertThat(user.getPassword()).isEqualTo(entity.getPassword());
        assertThat(user.getRoles()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(user.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }
}
