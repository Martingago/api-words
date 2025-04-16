package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta con listado de strings como contenido.")
public class ListStringApiResponseExample extends ApiResponseDTO<List<String>> {

    @Schema(description = "Listado de palabras encontradas.",
            example = "[\"débil\", \"blando\", \"frágil\"]")
    private List<String> responseObject;

    protected ListStringApiResponseExample(boolean status, String message, int serverCode, List<String> responseObject) {
        super(status, message, serverCode, responseObject);
        this.responseObject = responseObject;
    }
}
