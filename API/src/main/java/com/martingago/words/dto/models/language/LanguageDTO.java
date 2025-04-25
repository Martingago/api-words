package com.martingago.words.dto.models.language;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDTO {
    @Schema(description = "Código del idioma, siempre en minúsculas", example = "esp")
    private String lang;

    @Schema(description = "Cadena de texto con el idioma", example = "Español")
    private String language;
}
