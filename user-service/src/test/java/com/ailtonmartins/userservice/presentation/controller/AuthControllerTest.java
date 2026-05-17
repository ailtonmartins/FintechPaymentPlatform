package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.command.LoginCommand;
import com.ailtonmartins.userservice.application.command.RefreshTokenCommand;
import com.ailtonmartins.userservice.application.command.RegisterUserCommand;
import com.ailtonmartins.userservice.application.result.AuthResult;
import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.LoginUseCase;
import com.ailtonmartins.userservice.application.usecase.RefreshAccessTokenUseCase;
import com.ailtonmartins.userservice.application.usecase.RegisterUserUseCase;
import com.ailtonmartins.userservice.domain.exception.InvalidCredentialsException;
import com.ailtonmartins.userservice.domain.exception.UserAlreadyExistsException;
import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void deveCadastrarUsuario() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResult result = new UserResult(
                userId,
                "Ailton Martins",
                "ailton@email.com",
                Set.of(Role.USER),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(registerUserUseCase.execute(any(RegisterUserCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ailton Martins",
                                  "email": "ailton@email.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Ailton Martins"))
                .andExpect(jsonPath("$.email").value("ailton@email.com"))
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.active").value(true));

        verify(registerUserUseCase).execute(any(RegisterUserCommand.class));
    }

    @Test
    void deveRetornarBadRequestQuandoCadastroForInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "email-invalido",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requisicao invalida"))
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    void deveRetornarConflictQuandoEmailJaExistir() throws Exception {
        when(registerUserUseCase.execute(any(RegisterUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException("ailton@email.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ailton Martins",
                                  "email": "ailton@email.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ja existe um usuario cadastrado com o e-mail: ailton@email.com"));
    }

    @Test
    void deveAutenticarUsuario() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        AuthResult result = new AuthResult(userId, "access-token", "refresh-token", expiresAt);
        when(loginUseCase.execute(any(LoginCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ailton@email.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void deveRetornarUnauthorizedQuandoCredenciaisForemInvalidas() throws Exception {
        when(loginUseCase.execute(any(LoginCommand.class))).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ailton@email.com",
                                  "password": "senha-errada"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais invalidas"));
    }

    @Test
    void deveRenovarAccessToken() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        AuthResult result = new AuthResult(userId, "novo-access-token", "refresh-token", expiresAt);
        when(refreshAccessTokenUseCase.execute(any(RefreshTokenCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.accessToken").value("novo-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }
}
