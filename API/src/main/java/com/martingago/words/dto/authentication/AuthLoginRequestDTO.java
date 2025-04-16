package com.martingago.words.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos enviados por el usuario a autenticar en la aplicación.")
public record AuthLoginRequestDTO(
        @Schema(description = "Email del usuario a autenticar")
        @NotBlank String email,

        @Schema(description = "Contraseña del usuario a autenticar")
        @NotBlank String password) { }
