package com.martingago.words.service.word;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.definition.WordDefinitionService;
import com.martingago.words.service.language.LanguageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class WordInsertionService {

    @Autowired
    LanguageService languageService;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordDefinitionService wordDefinitionService;

    @Autowired
    WordService wordService;

    /**
     * Función que añade una palabra a la aplicación.
     * @param fullWordResponseDTO
     * @return
     */
    @Transactional
    public WordModel insertFullWord(FullWordRequestDTO fullWordResponseDTO){
        //Compruebo si el idioma de la palabra existe:
        LanguageModel languageModel;
        try {
            languageModel = languageService.searchLanguageByLangCode(fullWordResponseDTO.getLanguage());
        } catch (EntityNotFoundException e) {
            throw e;
        }
        //Comprueba que la palabra no exista en la BBDD:
        WordModel existingWord;
        try {
            existingWord = wordService.searchBasicWord(fullWordResponseDTO.getWord());
            // 3. Si la palabra existe, verifica si es un placeholder
            if (existingWord.isPlaceholder()) {
                // Actualiza el placeholder a false
                existingWord.setPlaceholder(false);
                existingWord = wordRepository.save(existingWord); // Guarda la actualización
            } else {
                // Si no es un placeholder, lanza una excepción o maneja el caso según tus necesidades
                throw new IllegalArgumentException("La palabra '" + fullWordResponseDTO.getWord() + "' ya existe en la base de datos y no es un placeholder.");
            }
        } catch (EntityNotFoundException e) {
            // 4. Si la palabra no existe, créala
            existingWord = WordModel.builder()
                    .word(fullWordResponseDTO.getWord())
                    .wordLength(fullWordResponseDTO.getLength())
                    .languageModel(languageModel)
                    .isPlaceholder(false) // No es un placeholder
                    .build();
            existingWord = wordRepository.save(existingWord); // Guarda la nueva palabra
        }

        //Extraer las definiciones de la palabra recibida:
        Set<WordDefinitionDTO> definitionDTOS = fullWordResponseDTO.getDefinitions();

        //Genera las definiciones de cada palabra
        Set<WordDefinitionModel> addedDefinitions = wordDefinitionService.validateAndInsertDefinitions(existingWord, definitionDTOS, languageModel);
        existingWord.setWordDefinitionModelSet(addedDefinitions);
        return existingWord;
    }

}
