package com.ailtonmartins.accountservice.presentation.controller;

import com.ailtonmartins.accountservice.application.command.AccountOperationCommand;
import com.ailtonmartins.accountservice.application.command.CreateAccountCommand;
import com.ailtonmartins.accountservice.application.result.AccountResult;
import com.ailtonmartins.accountservice.application.usecase.CreateAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.CreditAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.DebitAccountUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByIdUseCase;
import com.ailtonmartins.accountservice.application.usecase.FindAccountByUserIdUseCase;
import com.ailtonmartins.accountservice.infrastructure.config.OpenApiConfig;
import com.ailtonmartins.accountservice.presentation.dto.request.MoneyOperationRequest;
import com.ailtonmartins.accountservice.presentation.dto.response.AccountResponse;
import com.ailtonmartins.accountservice.presentation.dto.response.ErrorResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Contas", description = "Endpoints de criacao, consulta e movimentacao de contas")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final FindAccountByIdUseCase findAccountByIdUseCase;
    private final FindAccountByUserIdUseCase findAccountByUserIdUseCase;
    private final CreditAccountUseCase creditAccountUseCase;
    private final DebitAccountUseCase debitAccountUseCase;

    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            FindAccountByIdUseCase findAccountByIdUseCase,
            FindAccountByUserIdUseCase findAccountByUserIdUseCase,
            CreditAccountUseCase creditAccountUseCase,
            DebitAccountUseCase debitAccountUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.findAccountByIdUseCase = findAccountByIdUseCase;
        this.findAccountByUserIdUseCase = findAccountByUserIdUseCase;
        this.creditAccountUseCase = creditAccountUseCase;
        this.debitAccountUseCase = debitAccountUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar conta", description = "Cria uma conta para o usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Usuario ja possui conta",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AccountResponse> create(Authentication authentication) {
        AccountResult result = createAccountUseCase.execute(new CreateAccountCommand(authenticatedUserId(authentication)));
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.from(result));
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar minha conta", description = "Retorna a conta do usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Conta encontrada")
    public ResponseEntity<AccountResponse> findMine(Authentication authentication) {
        AccountResult result = findAccountByUserIdUseCase.execute(authenticatedUserId(authentication));
        return ResponseEntity.ok(AccountResponse.from(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por id", description = "Retorna uma conta pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AccountResponse> findById(
            @Parameter(description = "Identificador da conta", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id
    ) {
        AccountResult result = findAccountByIdUseCase.execute(id);
        return ResponseEntity.ok(AccountResponse.from(result));
    }

    @PostMapping("/{id}/credit")
    @Operation(summary = "Creditar valor", description = "Adiciona saldo a uma conta.")
    @ApiResponse(responseCode = "200", description = "Credito realizado")
    public ResponseEntity<AccountResponse> credit(
            @PathVariable UUID id,
            @Valid @RequestBody MoneyOperationRequest request
    ) {
        AccountResult result = creditAccountUseCase.execute(new AccountOperationCommand(id, request.amount()));
        return ResponseEntity.ok(AccountResponse.from(result));
    }

    @PostMapping("/{id}/debit")
    @Operation(summary = "Debitar valor", description = "Remove saldo de uma conta quando houver saldo suficiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Debito realizado"),
            @ApiResponse(
                    responseCode = "422",
                    description = "Saldo insuficiente ou conta inativa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AccountResponse> debit(
            @PathVariable UUID id,
            @Valid @RequestBody MoneyOperationRequest request
    ) {
        AccountResult result = debitAccountUseCase.execute(new AccountOperationCommand(id, request.amount()));
        return ResponseEntity.ok(AccountResponse.from(result));
    }

    private static UUID authenticatedUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
            throw new IllegalArgumentException("Usuario autenticado invalido");
        }
        return userId;
    }
}
