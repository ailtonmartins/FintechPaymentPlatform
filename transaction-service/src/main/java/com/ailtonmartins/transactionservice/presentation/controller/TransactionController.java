package com.ailtonmartins.transactionservice.presentation.controller;

import com.ailtonmartins.transactionservice.application.command.RequestTransferCommand;
import com.ailtonmartins.transactionservice.application.result.TransactionResult;
import com.ailtonmartins.transactionservice.application.usecase.FindTransactionByIdUseCase;
import com.ailtonmartins.transactionservice.application.usecase.RequestTransferUseCase;
import com.ailtonmartins.transactionservice.infrastructure.config.OpenApiConfig;
import com.ailtonmartins.transactionservice.presentation.dto.request.TransferRequest;
import com.ailtonmartins.transactionservice.presentation.dto.response.ErrorResponse;
import com.ailtonmartins.transactionservice.presentation.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transacoes", description = "Endpoints de solicitacao e consulta de transacoes")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class TransactionController {

    private static final String AUTHENTICATED_USER_ID_HEADER = "X-Authenticated-User-Id";

    private final RequestTransferUseCase requestTransferUseCase;
    private final FindTransactionByIdUseCase findTransactionByIdUseCase;

    public TransactionController(
            RequestTransferUseCase requestTransferUseCase,
            FindTransactionByIdUseCase findTransactionByIdUseCase
    ) {
        this.requestTransferUseCase = requestTransferUseCase;
        this.findTransactionByIdUseCase = findTransactionByIdUseCase;
    }

    @PostMapping("/transfers")
    @Operation(summary = "Solicitar transferencia", description = "Cria uma transacao PENDING e solicita o processamento da transferencia.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transferencia solicitada"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisicao invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TransactionResponse> requestTransfer(
            @Parameter(hidden = true)
            @RequestHeader(AUTHENTICATED_USER_ID_HEADER) UUID requesterUserId,
            @Valid @RequestBody TransferRequest request
    ) {
        TransactionResult result = requestTransferUseCase.execute(new RequestTransferCommand(
                requesterUserId,
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transacao por id", description = "Retorna uma transacao pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacao encontrada"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transacao nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TransactionResponse> findById(
            @Parameter(description = "Identificador da transacao", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id
    ) {
        TransactionResult result = findTransactionByIdUseCase.execute(id);
        return ResponseEntity.ok(TransactionResponse.from(result));
    }
}
