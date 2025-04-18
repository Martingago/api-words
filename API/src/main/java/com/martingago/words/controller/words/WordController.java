package com.martingago.words.controller.words;

import com.martingago.words.context.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.domain.service.qualification.WordQualificationService;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;
import com.martingago.words.dto.models.word.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.microservices.ExternalWordMapper;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.utils.CsvValidation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
public class WordController {

    private final CreateWordModelService createWordModelService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final WordQualificationService wordQualificationService;
    private final MyScrapWordClient myScrapWordClient;
    private final WordMapper wordMapper;
    private final ExternalWordMapper externalWordMapper;



    /**
     *  Scrapea una palabra recibida por el usuario, la añade a la base de datos, y se la muestra al usuario
     *  Valida que la palabra exista en la base de datos, si ya existe, informa al usuario.
     *  En caso de no existir, llama al microservicio de scraping para procesar la palabra y añadirla a la base de datos.
     * @param word string de la palabra que se quiere añadir en la base de datos.
     * @return
     */
    @Hidden
    @PostMapping("/scrap-word")
    public ResponseEntity<ApiResponseDTO<Object>> scrapWord(@RequestBody String word) {

        //Antes de iniciar el proceso de scrapping comprueba que la palabra no exista y si existe que sea un placeholder:
        WordValidator wordValidator = wordService.isWordLocatedAndNotPlaceholder(word);

        if (wordValidator.isExists()) {
            return ApiResponseDTO.build(
                    true,
                    "Word already exists on database",
                    HttpStatus.CONFLICT.value(),
                    wordMapper.toResponseDTO(wordValidator.getWordModel()),
                    HttpStatus.CONFLICT
            );
        }

        //Si no encuentra la palabra usa el micro-servicio > procesa > sube palabra
        ExternalBaseWordDTO externalBaseWordDTO = myScrapWordClient.procesarPalabra(word);
        // Comprueba si lo que recibe del microservicio es una full o related word
        if (externalBaseWordDTO instanceof RelatedWordDTOExternal relatedWordResponse) {
            return ApiResponseDTO.build(
                    false,
                    "Couldn't add word '" + word + "', did you mean: '" + relatedWordResponse.getRelatedWord() + "'?",
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    relatedWordResponse,
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        } else if (externalBaseWordDTO instanceof WordDTOExternal) {
            WordDTOExternal fullWordResponseDTO = (WordDTOExternal) externalBaseWordDTO;
            WordDTO wordDTO = externalWordMapper.toInternalDTO(fullWordResponseDTO);

            Map<String, LanguageModel> languageModelMap = languageService.getAllLanguagesMappedByLangCode(); //Obtiene información de los idiomas de la base de datos.
            Map<String, WordQualificationModel> wordQualificationModelMap = wordQualificationService.getAllQualificationsMapped(); //Obtiene información de las qualifications de la base de datos.
            Map<String, WordModel> newWordsModelToPersist = new HashMap<>(); //Instancia un map de palabras relacionadas que van a ser persistidas
            Map<String, SimpleWordSerializableDTO> existingDBWordsMap = wordService.findReferencesFromWordDTO(wordDTO); //Busca palabras relacionadas existentes en la Base de datos.


            WordModel wordModel = createWordModelService.processWordDTOintoWordModel(wordDTO,
                    languageModelMap,
                    wordQualificationModelMap,
                    newWordsModelToPersist,
                    existingDBWordsMap);


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
