package com.martingago.words.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO(@NotBlank String email,
                                  @NotBlank String password) {
}
