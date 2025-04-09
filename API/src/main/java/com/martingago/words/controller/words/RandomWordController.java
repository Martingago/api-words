package com.martingago.words.controller.words;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordApiResponse;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.service.word.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
    @Operation(summary = "/word",
            description = "Método 'GET' que obtiene una palabra aleatoria de la API de WordRadar.",
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
    @GetMapping("/word")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> getRandomWord(
            @Parameter(description = "Longitud de la palabra aleatoria",
                    required = false,
                    example = "5")
            @RequestParam(value = "length", required = false) Integer wordLength
    ) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getRandomWord(wordLength);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }
}
