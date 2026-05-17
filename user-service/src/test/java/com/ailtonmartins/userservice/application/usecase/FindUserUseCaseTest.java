package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.exception.UserNotFoundException;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void deveBuscarUsuarioPorId() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        FindUserByIdUseCase useCase = new FindUserByIdUseCase(userRepository);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResult result = useCase.execute(user.getId());

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo("ailton@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoPorId() {
        UUID userId = UUID.randomUUID();
        FindUserByIdUseCase useCase = new FindUserByIdUseCase(userRepository);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuario nao encontrado para o id: " + userId);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        User user = new User("Ailton Martins", "ailton@email.com", "senha-criptografada");
        FindUserByEmailUseCase useCase = new FindUserByEmailUseCase(userRepository);

        when(userRepository.findByEmail("ailton@email.com")).thenReturn(Optional.of(user));

        UserResult result = useCase.execute("ailton@email.com");

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo("ailton@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoPorEmail() {
        FindUserByEmailUseCase useCase = new FindUserByEmailUseCase(userRepository);

        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("inexistente@email.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuario nao encontrado para o e-mail: inexistente@email.com");
    }
}
