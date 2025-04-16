package com.martingago.words.controller.words.search;

import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.models.word.response.WordResponseViewDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.DailyWordService;
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
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
public class DailyWordController {

    private final DailyWordService dailyWordService;
    private final WordMapper wordMapper;

    @Operation(
            summary = "Obtener la palabra diaria",
            description = "Método 'GET' que obtiene la palabra diaria generada automáticamente por el servidor. La palabra diaria se actualiza a las 00:00 CET (Madrid).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Palabra diaria obtenida correctamente.",
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

    @GetMapping("/daily")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> getDailyWord() {
        WordModel wordModel = dailyWordService.getDailyWord();
        WordResponseViewDTO wordResponseViewDTO = wordMapper.toResponseDTO(wordModel);
        return ApiResponseDTO.build(true,
                "Daily word founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }
}
