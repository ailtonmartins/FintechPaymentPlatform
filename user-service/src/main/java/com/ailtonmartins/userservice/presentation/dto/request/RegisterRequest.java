package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.RegisterUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de usuario")
public record RegisterRequest(
        @Schema(description = "Nome completo do usuario", example = "Ailton Martins")
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String name,

        @Schema(description = "E-mail usado para login", example = "ailton@email.com")
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ser valido")
        @Size(max = 180, message = "E-mail deve ter no maximo 180 caracteres")
        String email,

        @Schema(description = "Senha do usuario", example = "123456", minLength = 6, maxLength = 72)
        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, max = 72, message = "Senha deve ter entre 6 e 72 caracteres")
        String password
) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(name, email, password);
    }
}
