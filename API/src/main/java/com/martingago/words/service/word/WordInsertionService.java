package com.martingago.words.service.word;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import com.martingago.words.repository.WordRepository;
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
    WordQualificationRepository wordQualificationRepository;

    @Autowired
    WordQualificationService wordQualificationService;

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

        //Extrer las qualifications de la palabra recibida:
        Set<String> qualifications =  wordResponseDTO.getDefinitions().stream()
                .map(WordDefinitionDTO::getQualification)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

       //Valida las qualification y devuelve las qualifications que componen las definiciones:
        Map<String, WordQualificationModel> qualificationModelMap = wordQualificationService.validateAndInsertQualifications(qualifications);


        WordModel wordModel = WordModel.builder()
                .word(wordResponseDTO.getWord())
                .wordLength(wordResponseDTO.getLength())
                .languageModel(languageModel)
                .build();
        System.out.println("Inserted: " + wordModel.toString());
        return wordRepository.save(wordModel);
    }



}
