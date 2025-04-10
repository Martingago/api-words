package com.martingago.words.controller.relations;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.ListStringApiResponse;
import com.martingago.words.domain.model.RelationEnumType;
import com.martingago.words.domain.service.relation.WordRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@Tag(   name = "Relaciones de palabras",
        description = "Operaciones relacionadas con la búsqueda de relaciones de palabras con otras.")
public class SynonymsSearchController {


    private final WordRelationService wordRelationService;

    /**
     * Controller que recibe como parámetro una palabra y realiza una búsqueda de sus sinónimos.
     * @param word
     * @return
     */
    @Operation(summary = "/synonyms/{word}",
            description = "Método 'GET' que busca qué sinónimos tiene una palabra.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListStringApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "404",
                            description = "Not found"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/synonyms/{word}")
    public ResponseEntity<ApiResponseDTO<List<String>>> findSynonymsByWord(
            @Parameter(description = "Palabra de la que se quieren buscar los sinónimos.",
                    required = true,
                    example = "oscuro")
            @PathVariable String word,

            @Parameter(description = "Código de idioma de la palabra sobre la que realizar la búsqueda.",
                    required = false,
                    example = "esp")
            @RequestParam(value = "lang", defaultValue = "esp") String langCode){
        List<String> listSynonyms = wordRelationService.getRelationTypeByWord(word, RelationEnumType.SINONIMA, langCode);
        return ApiResponseDTO.build(
                true,
                "Synonyms founded ",
                HttpStatus.OK.value(),
                listSynonyms,
                HttpStatus.OK
        );

    }

}
