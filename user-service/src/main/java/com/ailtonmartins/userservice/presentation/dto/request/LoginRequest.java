package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para autenticacao de usuario")
public record LoginRequest(
        @Schema(description = "E-mail cadastrado", example = "ailton@email.com")
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ser valido")
        String email,

        @Schema(description = "Senha do usuario", example = "123456")
        @NotBlank(message = "Senha e obrigatoria")
        String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
