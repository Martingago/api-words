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
    @Operation(summary = "/search/{word}",
            description = "Método 'GET' que busca una palabra  específica en la API de WordRadar.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "404",
                            description = "Not found"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> findWordByName(
            @Parameter(description = "Palabra que quiere ser buscada en la Base de datos de WordRadar.",
                    required = true,
                    example = "piedra")
            @PathVariable String word
    ) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getWordByName(word);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }

}
