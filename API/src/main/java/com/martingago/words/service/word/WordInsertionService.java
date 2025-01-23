package com.martingago.words.service.word;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.WordQualificationDTO;
import com.martingago.words.dto.word.WordCreationDTO;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.language.LanguageService;
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

        //Comprobar cuales de esas qualifications ya existen en la BBDD.
        Set<WordQualificationModel> existingQualifications = new HashSet<>(
                wordQualificationRepository.findByQualificationIn(qualifications));

        // Prepare new qualifications for batch insert
        List<WordQualificationModel> newQualifications = qualifications.stream()
                .filter(q -> existingQualifications.stream()
                        .noneMatch(existing -> existing.getQualification().equals(q)))
                .map(qualification -> WordQualificationModel.builder().qualification(qualification).build())
                .collect(Collectors.toList());

        wordQualificationRepository.saveAll(newQualifications);

        // Determinar las qualifications que faltan en la base de datos
        Set<String> existingQualificationStrings = existingQualifications.stream()
                .map(WordQualificationModel::getQualification)
                .collect(Collectors.toSet());

        Set<String> missingQualifications = new HashSet<>(qualifications);
        missingQualifications.removeAll(existingQualificationStrings);






        WordModel wordModel = WordModel.builder()
                .word(wordResponseDTO.getWord())
                .wordLength(wordResponseDTO.getLength())
                .languageModel(languageModel)
                .build();
        System.out.println("Inserted: " + wordModel.toString());
        return wordRepository.save(wordModel);
    }

}
