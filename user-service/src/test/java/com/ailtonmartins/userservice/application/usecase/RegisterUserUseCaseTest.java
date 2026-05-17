package com.ailtonmartins.userservice.application.usecase;

import com.ailtonmartins.userservice.application.command.RegisterUserCommand;
import com.ailtonmartins.userservice.application.port.PasswordHasher;
import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.domain.exception.UserAlreadyExistsException;
import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void deveCadastrarUsuarioComSenhaCriptografadaERoleUser() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Ailton Martins",
                "ailton@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordHasher.hash(command.password())).thenReturn("senha-criptografada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResult result = registerUserUseCase.execute(command);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo("Ailton Martins");
        assertThat(savedUser.getEmail()).isEqualTo("ailton@email.com");
        assertThat(savedUser.getPassword()).isEqualTo("senha-criptografada");
        assertThat(savedUser.getRoles()).containsExactly(Role.USER);
        assertThat(savedUser.isActive()).isTrue();

        assertThat(result.id()).isEqualTo(savedUser.getId());
        assertThat(result.email()).isEqualTo("ailton@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Ailton Martins",
                "ailton@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Ja existe um usuario cadastrado com o e-mail: ailton@email.com");

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any());
    }
}
