package com.martingago.words.dto.authentication;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del servidor tras autenticar un usuario.")
@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponseDTO(
        @Schema(description = "Correo electrónico del usuario autenticado.")
        String username,

        @Schema(description = "Mensaje de estado de la autenticación.")
        String message,

        @Schema(description = "Token JWT generado para la sesión del usuario (válido durante 30 minutos).")
        String jwt,

        @Schema(description = "Indica si la autenticación fue exitosa.")
        boolean status) { }
