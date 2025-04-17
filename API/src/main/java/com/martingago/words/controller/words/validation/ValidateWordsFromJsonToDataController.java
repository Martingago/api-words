package com.martingago.words.controller.words.validation;

import com.martingago.words.domain.service.word.CheckWordsInBatchesService;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordValidationResponseExample;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Validar palabras",
        description = "Operaciones relacionadas con las validaciones de palabras en la API de WordRadar")
public class ValidateWordsFromJsonToDataController {

    private final CheckWordsInBatchesService checkWordsInBatchesService;

    @Operation(
            summary = "Validar palabras desde un body JSON y devolver resultado en JSON",
            description = """
        Recibe un body application/json con un array de palabras, valida su existencia en base de datos y devuelve los resultados en formato JSON.

        📥 Formato de entrada:
        - Content-Type: application/json
        - Estructura: array de strings
   
        📤 Formato de respuesta:
        - JSON con un objeto 'responseObject' tipo Map<String, Boolean>
        - Cada palabra enviada como clave y true/false como valor según exista o no en la base de datos.
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Validación realizada correctamente. Respuesta en JSON con el estado de cada palabra.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordValidationResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Body inválido. Array vacío, mal formateado o inexistente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 400",
                                            value = ApiErrorExamples.ERROR_400
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar la solicitud.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 500",
                                            value = ApiErrorExamples.ERROR_500
                                    )
                            )
                    )
            }
    )

    @PostMapping(value = "/validate/body/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<Map<String, Boolean>>> validateWordsJson(@RequestBody @NotEmpty List<String> words) throws IOException {
        Map<String, Boolean> validatedWords = checkWordsInBatchesService.checkWordsInBatches(new HashSet<>(words), 100);

        return ApiResponseDTO.build(true,
                "Words validated successfully.",
                HttpStatus.OK.value(),
                validatedWords,
                HttpStatus.OK);
    }
}
