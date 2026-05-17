package com.ailtonmartins.userservice.infrastructure.persistence.adapter;

import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.infrastructure.persistence.entity.UserEntity;
import com.ailtonmartins.userservice.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void deveSalvarUsuarioUsandoJpaRepository() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        when(jpaUserRepository.save(org.mockito.ArgumentMatchers.any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userRepositoryAdapter.save(user);

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(jpaUserRepository).save(entityCaptor.capture());

        assertThat(entityCaptor.getValue().getEmail()).isEqualTo("ailton@email.com");
        assertThat(entityCaptor.getValue().getRoles()).containsExactly(Role.USER);
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
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

        when(jpaUserRepository.findByEmail("ailton@email.com")).thenReturn(Optional.of(entity));

        Optional<User> user = userRepositoryAdapter.findByEmail("ailton@email.com");

        assertThat(user).isPresent();
        assertThat(user.get().getRoles()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
    }

    @Test
    void deveVerificarSeEmailExiste() {
        when(jpaUserRepository.existsByEmail("ailton@email.com")).thenReturn(true);

        boolean exists = userRepositoryAdapter.existsByEmail("ailton@email.com");

        assertThat(exists).isTrue();
    }
}
