package com.martingago.words.controller.words;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.domain.service.qualification.WordQualificationService;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.WordBatchDTO;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordInsertionService;
import io.swagger.v3.oas.annotations.Hidden;
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
@Hidden
public class AddWordController {


    private final LanguageService languageService;
    private final WordQualificationService wordQualificationService;
    private final CreateWordModelService createWordModelService;
    private final WordService wordService;
    private final WordMapper wordMapper;


    /**
     * Añade una palabra en la Base de datos.
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> insertWord(
            @RequestBody @Valid WordBatchDTO wordBatchDTO){

        //Obtener map de idiomas de la base de datos:
        Map<String, LanguageModel> languageModelMap = languageService.getAllLanguagesMappedByLangCode();
        Map<String, WordQualificationModel> wordQualificationModelMap = wordQualificationService.getAllQualificationsMapped();
        Map<String, WordModel> newWordsModelToPersist = new HashMap<>();
        Map<String, WordBatchReferenceDTO> existingDBWordsMap = wordService.findReferencesFromWordDTO(wordBatchDTO);

        //Obtiene el objeto WordModel procesado que contendrá la información de sus atributos.
        WordModel wordModel = createWordModelService.insertWordIntoDatabase(wordBatchDTO,
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
