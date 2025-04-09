package com.martingago.words.controller.words;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordApiResponse;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.word.DailyWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "/daily",
            description = "Método 'GET' que obtiene la palabra diaria generada por el servidor. La palabra diaria se genera a las 00:00 CET (Madrid)",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
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
