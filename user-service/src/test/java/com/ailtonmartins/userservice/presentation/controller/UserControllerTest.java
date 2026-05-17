package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByEmailUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.domain.exception.UserNotFoundException;
import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        UUID userId = UUID.randomUUID();
        when(findUserByIdUseCase.execute(userId)).thenReturn(userResult(userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("ailton@email.com"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistirPorId() throws Exception {
        UUID userId = UUID.randomUUID();
        when(findUserByIdUseCase.execute(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado para o id: " + userId));
    }

    @Test
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", "id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parametro invalido: id"));
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        UUID userId = UUID.randomUUID();
        when(findUserByEmailUseCase.execute("ailton@email.com")).thenReturn(userResult(userId));

        mockMvc.perform(get("/api/v1/users").param("email", "ailton@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("ailton@email.com"));
    }

    @Test
    void deveRetornarBadRequestQuandoEmailNaoForInformado() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parametro obrigatorio ausente: email"));
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistirPorEmail() throws Exception {
        when(findUserByEmailUseCase.execute(eq("inexistente@email.com")))
                .thenThrow(new UserNotFoundException("inexistente@email.com"));

        mockMvc.perform(get("/api/v1/users").param("email", "inexistente@email.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado para o e-mail: inexistente@email.com"));
    }

    private static UserResult userResult(UUID userId) {
        return new UserResult(
                userId,
                "Ailton Martins",
                "ailton@email.com",
                Set.of(Role.USER),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
