package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String name,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ser valido")
        @Size(max = 180, message = "E-mail deve ter no maximo 180 caracteres")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, max = 72, message = "Senha deve ter entre 6 e 72 caracteres")
        String password
) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(name, email, password);
    }
}
