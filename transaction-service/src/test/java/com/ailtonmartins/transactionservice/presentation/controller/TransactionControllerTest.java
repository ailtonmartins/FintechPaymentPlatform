package com.ailtonmartins.transactionservice.presentation.controller;

import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.application.usecase.FindTransactionByIdUseCase;
import com.ailtonmartins.transactionservice.application.usecase.RequestTransferUseCase;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.domain.model.TransactionStatus;
import com.ailtonmartins.transactionservice.domain.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RequestTransferUseCase requestTransferUseCase;

    @MockitoBean
    private FindTransactionByIdUseCase findTransactionByIdUseCase;

    @Test
    void deveSolicitarTransferencia() throws Exception {
        UUID requesterUserId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        when(requestTransferUseCase.execute(any())).thenReturn(transactionResult(
                transactionId,
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                TransactionStatus.PENDING
        ));

        mockMvc.perform(post("/api/v1/transactions/transfers")
                        .header("X-Authenticated-User-Id", requesterUserId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "sourceAccountId": "%s",
                                  "destinationAccountId": "%s",
                                  "amount": 100.00
                                }
                                """.formatted(sourceAccountId, destinationAccountId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.requesterUserId").value(requesterUserId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void deveRetornarBadRequestQuandoValorForInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/transfers")
                        .header("X-Authenticated-User-Id", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {
                                  "sourceAccountId": "%s",
                                  "destinationAccountId": "%s",
                                  "amount": 0
                                }
                                """.formatted(UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requisicao invalida"))
                .andExpect(jsonPath("$.fields[0].message").value("valor deve ser maior que zero"));
    }

    @Test
    void deveBuscarTransacaoPorId() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID requesterUserId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(transactionResult(
                transactionId,
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                TransactionStatus.PENDING
        ));

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void deveRetornarNotFoundQuandoTransacaoNaoExistir() throws Exception {
        UUID transactionId = UUID.randomUUID();
        when(findTransactionByIdUseCase.execute(transactionId)).thenThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transacao nao encontrada para o id: " + transactionId));
    }

    private static TransactionResult transactionResult(
            UUID transactionId,
            UUID requesterUserId,
            UUID sourceAccountId,
            UUID destinationAccountId,
            TransactionStatus status
    ) {
        return new TransactionResult(
                transactionId,
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                new BigDecimal("100.00"),
                TransactionType.TRANSFER,
                status,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
