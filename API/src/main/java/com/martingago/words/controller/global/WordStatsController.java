package com.martingago.words.controller.global;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.stats.StatsApiResponse;
import com.martingago.words.dto.global.stats.WordStatsDTO;
import com.martingago.words.service.global.WordsStaticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Estadísticas del API", description = "Operaciones relacionadas con obtener las estadísticas del API.")
public class WordStatsController {

    private final WordsStaticsService wordsStaticsService;


    @Operation(summary = "/stats",
            description = "Método 'GET' que obtiene estadísticas de las palabras de la API de WordRadar",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StatsApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<WordStatsDTO>> getWordsStatics(){
        WordStatsDTO statics = wordsStaticsService.getWordsStatics();
        return ApiResponseDTO.build(
                true,
                "Stats successfully founded",
                HttpStatus.OK.value(),
                statics,
                HttpStatus.OK);
    }
}
