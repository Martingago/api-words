package com.martingago.words.controller.words;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.domain.service.qualification.WordQualificationService;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.request.WordBatchDTO;
import com.martingago.words.dto.models.word.request.WordBatchReferenceDTO;
import com.martingago.words.dto.models.word.response.WordResponseViewDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Tag(   name = "Gestionar palabras",
        description = "Operaciones privadas relacionadas con la gestión de palabras en la API de WordRadar")
public class AddWordController {

    private final LanguageService languageService;
    private final WordQualificationService wordQualificationService;
    private final CreateWordModelService createWordModelService;
    private final WordService wordService;
    private final WordMapper wordMapper;


    /**
     * Añade una palabra a la base de datos
     * @param wordBatchDTO objeto DTO que se quiere añadir a la base de datos.
     * @return ApiResponseDTO que contiene un WordResponseViewDTO con la información que se ha añadido en la base de datos.
     */
    @Operation(
            summary = "Añadir una nueva palabra",
            description = "Método 'POST' para añadir una nueva palabra a la base de datos. El proceso involucra la validación y la creación de las referencias necesarias para la persistencia.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Palabra añadida correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordResponseViewDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición mal formada o datos incorrectos.",
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
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> insertWord(
            @RequestBody @Valid WordBatchDTO wordBatchDTO){

        Map<String, LanguageModel> languageModelMap = languageService.getAllLanguagesMappedByLangCode(); //Obtiene información de los idiomas de la base de datos.
        Map<String, WordQualificationModel> wordQualificationModelMap = wordQualificationService.getAllQualificationsMapped(); //Obtiene información de las qualifications de la base de datos.
        Map<String, WordModel> newWordsModelToPersist = new HashMap<>(); //Instancia un map de palabras relacionadas que van a ser persistidas
        Map<String, WordBatchReferenceDTO> existingDBWordsMap = wordService.findReferencesFromWordDTO(wordBatchDTO); //Busca palabras relacionadas existentes en la Base de datos.

        //Obtiene el objeto WordModel procesado que contendrá la información de sus atributos.
        WordModel wordModel = createWordModelService.processWordDTOintoWordModel(wordBatchDTO,
                languageModelMap,
                wordQualificationModelMap,
                newWordsModelToPersist,
                existingDBWordsMap
                );

        //Persiste la entidad en la base de datos.
        WordModel insertedWord = wordService.saveWordModel(wordModel);

        //Devuelve el objeto procesado al usuario.
        WordResponseViewDTO updatedWordResponseViewDTO = wordMapper.toResponseDTO(insertedWord);
        return ApiResponseDTO.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseViewDTO,
                HttpStatus.CREATED);
    }


}
