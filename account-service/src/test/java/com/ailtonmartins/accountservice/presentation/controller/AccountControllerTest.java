package com.ailtonmartins.accountservice.presentation.controller;

import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.application.usecase.CreateAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.CreditAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.DebitAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByIdUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByUserIdUseCase;
import com.ailtonmartins.accountservice.domain.exception.AccountNotFoundException;
import com.ailtonmartins.accountservice.domain.exception.InsufficientBalanceException;
import com.ailtonmartins.accountservice.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private FindAccountByIdUseCase findAccountByIdUseCase;

    @MockitoBean
    private FindAccountByUserIdUseCase findAccountByUserIdUseCase;

    @MockitoBean
    private CreditAccountUseCase creditAccountUseCase;

    @MockitoBean
    private DebitAccountUseCase debitAccountUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void deveCriarContaParaUsuarioAutenticado() throws Exception {
        UUID userId = UUID.randomUUID();
        AccountResult result = accountResult(UUID.randomUUID(), userId, BigDecimal.ZERO);
        when(createAccountUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/accounts").principal(authentication(userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(result.id().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void deveBuscarMinhaConta() throws Exception {
        UUID userId = UUID.randomUUID();
        AccountResult result = accountResult(UUID.randomUUID(), userId, new BigDecimal("10.00"));
        when(findAccountByUserIdUseCase.execute(userId)).thenReturn(result);

        mockMvc.perform(get("/api/v1/accounts/me").principal(authentication(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.accountNumber").value("12345678"));
    }

    @Test
    void deveBuscarContaPorId() throws Exception {
        UUID accountId = UUID.randomUUID();
        AccountResult result = accountResult(accountId, UUID.randomUUID(), new BigDecimal("25.00"));
        when(findAccountByIdUseCase.execute(accountId)).thenReturn(result);

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(25.00));
    }

    @Test
    void deveRetornarNotFoundQuandoContaNaoExistir() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(findAccountByIdUseCase.execute(accountId)).thenThrow(new AccountNotFoundException(accountId));

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conta nao encontrada para o id: " + accountId));
    }

    @Test
    void deveCreditarConta() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(creditAccountUseCase.execute(any())).thenReturn(accountResult(accountId, UUID.randomUUID(), new BigDecimal("100.00")));

        mockMvc.perform(post("/api/v1/accounts/{id}/credit", accountId)
                        .contentType("application/json")
                        .content("{\"amount\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    void deveRetornarBadRequestQuandoValorForInvalido() throws Exception {
        UUID accountId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/accounts/{id}/credit", accountId)
                        .contentType("application/json")
                        .content("{\"amount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requisicao invalida"))
                .andExpect(jsonPath("$.fields[0].message").value("valor deve ser maior que zero"));
    }

    @Test
    void deveRetornarUnprocessableEntityQuandoSaldoForInsuficiente() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(debitAccountUseCase.execute(any())).thenThrow(new InsufficientBalanceException());

        mockMvc.perform(post("/api/v1/accounts/{id}/debit", accountId)
                        .contentType("application/json")
                        .content("{\"amount\":100.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"));
    }

    private static UsernamePasswordAuthenticationToken authentication(UUID userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, List.of());
    }

    private static AccountResult accountResult(UUID accountId, UUID userId, BigDecimal balance) {
        return new AccountResult(
                accountId,
                userId,
                "12345678",
                balance,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
