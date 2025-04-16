package com.martingago.words.controller.words.search;

import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.models.word.response.WordDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
public class RandomWordController {

    private final WordService wordService;

    /**
     * Obtiene una palabra aleatoria de toda la base de datos de palabras
     *
     * @param wordLength tamaño de la palabra que se quiere obtener aleatoriamente
     * @return Objeto ApiResponseDTO que contiene la información de la palabra aleatoria obtenida.
     */
    @Operation(
            summary = "Obtener una palabra aleatoria",
            description = "Método 'GET' que obtiene una palabra aleatoria de la base de datos de la API de WordRadar. El tamaño de la palabra se puede especificar (opcionalmente).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Palabra aleatoria obtenida correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición mal formada.",
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
    @GetMapping("/word")
    public ResponseEntity<ApiResponseDTO<WordDTO>> getRandomWord(
            @Parameter(description = "Longitud de la palabra aleatoria",
                    required = false,
                    example = "5")
            @RequestParam(value = "length", required = false) Integer wordLength
    ) {
        WordDTO wordDTO = wordService.getRandomWord(wordLength);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordDTO,
                HttpStatus.OK);
    }
}
