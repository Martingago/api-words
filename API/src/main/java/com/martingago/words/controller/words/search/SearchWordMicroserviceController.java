package com.martingago.words.controller.words.search;

import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.context.WordValidator;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.batch.ProcessWordModelService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;
import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.microservices.word.external.WordToScrapDTOExternal;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class SearchWordMicroserviceController {

    private final WordService wordService;
    private final WordMapper wordMapper;
    private final MyScrapWordClient myScrapWordClient;
    private final ProcessWordModelService processWordModelService;

    @GetMapping("/deep-search/{word}")
    public ResponseEntity<ApiResponseDTO<Object>> findWordByName(
            @Parameter(description = "Palabra que se desea buscar en la base de datos de WordRadar.",
                    required = true,
                    example = "piedra")
            @PathVariable String word
    ) {
        try {

            WordDTO wordDTO = wordService.getWordByName(word);
            return ApiResponseDTO.build(true,
                    "Word successfully founded",
                    HttpStatus.OK.value(),
                    wordDTO,
                    HttpStatus.OK);
        }catch (EntityNotFoundException ex){
            //Se scrapea la palabra del usuario, y si se ha encontrado se añade a la base de datos.
            WordToScrapDTOExternal wordToScrapDTOExternal = new WordToScrapDTOExternal(word);

            ExternalBaseWordDTO externalBaseWordDTO = myScrapWordClient.procesarPalabra(wordToScrapDTOExternal);
            // Comprueba si lo que recibe del microservicio es una full o related word
            if (externalBaseWordDTO instanceof RelatedWordDTOExternal relatedWordResponse) {
                return ApiResponseDTO.build(
                        false,
                        "Couldn't add word '" + wordToScrapDTOExternal.getWord() + "', did you mean: '" + relatedWordResponse.getRelatedWord() + "'?",
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        relatedWordResponse,
                        HttpStatus.UNPROCESSABLE_ENTITY
                );
            } else if (externalBaseWordDTO instanceof WordDTOExternal) {
                WordDTOExternal fullWordResponseDTO = (WordDTOExternal) externalBaseWordDTO;
                WordDTO wordDTO = wordMapper.toInternalDTO(fullWordResponseDTO);
                WordModel wordModel = processWordModelService.processWordDTO(wordDTO);

                //Guarda la entidad en la base de datos.
                wordService.saveWordModel(wordModel);

                return ApiResponseDTO.build(
                        true,
                        "Word successfully validate and added",
                        HttpStatus.CREATED.value(),
                        wordMapper.toResponseDTO(wordModel),
                        HttpStatus.CREATED);
            }
            return ApiResponseDTO.build(
                    false,
                    "Invalid Object to upload on database",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    externalBaseWordDTO,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        }
    }

}
