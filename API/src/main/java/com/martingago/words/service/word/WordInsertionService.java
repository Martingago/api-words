package com.martingago.words.service.word;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.WordQualificationDTO;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.definition.WordDefinitionService;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.qualification.WordQualificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WordInsertionService {

    @Autowired
    LanguageService languageService;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordDefinitionService wordDefinitionService;

    /**
     *
     * @param wordResponseDTO
     * @return
     */
    @Transactional
    public WordModel insertFullWord(WordResponseDTO wordResponseDTO){
        //Compruebo si el idioma de la palabra existe:
        LanguageModel languageModel;
        try{
            languageModel= languageService.searchLanguageByLangCode(wordResponseDTO.getLanguage());
        }catch (EntityNotFoundException e){
            throw  e;
        }

        //Extraer las definiciones de la palabra recibida:
        Set<WordDefinitionDTO> definitionDTOS = wordResponseDTO.getDefinitions();

        WordModel wordModel = WordModel.builder()
                .word(wordResponseDTO.getWord())
                .wordLength(wordResponseDTO.getLength())
                .languageModel(languageModel)
                .build();
        System.out.println("Inserted: " + wordModel.toString());
        WordModel newWord=  wordRepository.save(wordModel);

        //Genera las definiciones de cada palabra
        wordDefinitionService.validateAndInsertDefinitions(newWord, definitionDTOS);

        return newWord;
    }



}
