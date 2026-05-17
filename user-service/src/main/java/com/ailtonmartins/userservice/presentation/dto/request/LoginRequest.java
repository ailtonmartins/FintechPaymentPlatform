package com.ailtonmartins.userservice.presentation.dto.request;

import com.ailtonmartins.userservice.application.command.LoginCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail deve ser valido")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
