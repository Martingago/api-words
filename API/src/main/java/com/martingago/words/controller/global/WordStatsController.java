package com.martingago.words.controller.global;

import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.StatsApiResponseExample;
import com.martingago.words.dto.global.stats.WordStatsDTO;
import com.martingago.words.domain.service.global.WordsStaticsService;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Estadísticas del API",
        description = "Operaciones relacionadas con obtener las estadísticas del API.")
public class WordStatsController {

    private final WordsStaticsService wordsStaticsService;


    /**
     * Endpoint que obtiene información de las estadísticas obtenidas del API.
     * @return WordStatsDTO con la información de las palabras existentes en la base de datos.
     */
    @Operation(
            summary = "Obtener estadísticas generales del API",
            description = "Método 'GET' que devuelve las estadísticas generales de la API de WordRadar: número de palabras, definiciones, sinónimos, antónimos y ejemplos registrados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estadísticas obtenidas correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StatsApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición incorrecta.",
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
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<WordStatsDTO>> getWordsStatics(){
        WordStatsDTO statics = wordsStaticsService.getWordsStatics(); //Obtiene las estadísticas del API
        return ApiResponseDTO.build(
                true,
                "Stats successfully founded",
                HttpStatus.OK.value(),
                statics,
                HttpStatus.OK);
    }
}
