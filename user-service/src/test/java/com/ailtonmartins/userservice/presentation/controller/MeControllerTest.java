package com.ailtonmartins.userservice.presentation.controller;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {

    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveBuscarUsuarioAutenticado() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null, List.of())
        );
        when(findUserByIdUseCase.execute(USER_ID)).thenReturn(new UserResult(
                USER_ID,
                "Ailton Martins",
                "ailton@email.com",
                Set.of(Role.USER),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.email").value("ailton@email.com"));
    }
}
