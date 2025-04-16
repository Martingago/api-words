package com.martingago.words.controller.words.search;

import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
public class SearchWordController {

    private final WordService wordService;

    /**
     * Busca en la base de datos una palabra
     * @param word string de la palabra que se quiere buscar en la base de datos
     * @return Objeto ApiResponseDTO que contiene la información de la palabra encontrada.
     */
    @Operation(
            summary = "Buscar una palabra en base de datos",
            description = "Método 'GET' que busca una palabra específica en la base de datos de WordRadar y devuelve su información completa si está registrada.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Palabra encontrada con éxito.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetro de búsqueda no válido.",
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
                            responseCode = "404",
                            description = "No se encontró la palabra solicitada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 404",
                                            value = ApiErrorExamples.ERROR_404
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor.",
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
    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponseDTO<WordDTO>> findWordByName(
            @Parameter(description = "Palabra que se desea buscar en la base de datos de WordRadar.",
                    required = true,
                    example = "piedra")
            @PathVariable String word
    ) {
        WordDTO wordDTO = wordService.getWordByName(word);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordDTO,
                HttpStatus.OK);
    }

}
