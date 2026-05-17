package com.ailtonmartins.userservice.infrastructure.config;

import com.ailtonmartins.userservice.application.result.UserResult;
import com.ailtonmartins.userservice.application.usecase.FindUserByEmailUseCase;
import com.ailtonmartins.userservice.application.usecase.FindUserByIdUseCase;
import com.ailtonmartins.userservice.domain.model.Role;
import com.ailtonmartins.userservice.domain.model.User;
import com.ailtonmartins.userservice.domain.repository.UserRepository;
import com.ailtonmartins.userservice.infrastructure.security.JwtAccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtAccessTokenProvider jwtAccessTokenProvider;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void devePermitirUserControllerParaAdmin() throws Exception {
        User admin = new User("Admin", "admin@email.com", "senha-criptografada");
        admin.addRole(Role.ADMIN);
        String token = jwtAccessTokenProvider.generate(admin);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(findUserByEmailUseCase.execute("ailton@email.com")).thenReturn(userResult());

        mockMvc.perform(get("/api/v1/users")
                        .param("email", "ailton@email.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarUserControllerParaUserSemAdmin() throws Exception {
        User user = new User("User", "user@email.com", "senha-criptografada");
        String token = jwtAccessTokenProvider.generate(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users")
                        .param("email", "ailton@email.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirMeParaUserAutenticado() throws Exception {
        User user = new User("User", "user@email.com", "senha-criptografada");
        String token = jwtAccessTokenProvider.generate(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(findUserByIdUseCase.execute(user.getId())).thenReturn(new UserResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        ));

        mockMvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarMeSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarUserControllerSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("email", "ailton@email.com"))
                .andExpect(status().isForbidden());
    }

    private static UserResult userResult() {
        return new UserResult(
                UUID.randomUUID(),
                "Ailton Martins",
                "ailton@email.com",
                Set.of(Role.USER),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
