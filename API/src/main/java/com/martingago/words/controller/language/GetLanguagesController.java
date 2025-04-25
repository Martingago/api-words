package com.martingago.words.controller.language;


import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.dto.docs.LanguageListApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.language.LanguageDTO;
import com.martingago.words.mapper.models.LanguageMapper;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Tag(   name ="Idiomas", description = "Controlador que permite obtener información de los idiomas existentes en la base de datos.")
public class GetLanguagesController {

    private final LanguageService languageService;
    private final LanguageMapper languageMapper;

    @GetMapping("/languages")
    @Operation(
            summary = "Obtener idiomas disponibles en base de datos",
            description = "Devuelve una lista con todos los idiomas registrados en la base de datos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de idiomas obtenido correctamente.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LanguageListApiResponseExample.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al obtener los idiomas.",
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
    public ResponseEntity<ApiResponseDTO<List<LanguageDTO>>> getLanguagesInDatabase(){
        List<LanguageDTO> languageDTOList =  languageService.getLanguagesFromDatabase()
                .stream().map(languageMapper::toDTO)
                .toList();

        return ApiResponseDTO.build(
                true,
                "Languages retrieved successfully.",
                HttpStatus.OK.value(),
                languageDTOList,
                HttpStatus.OK
        );
    }
}
