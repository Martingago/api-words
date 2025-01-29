package com.martingago.words.service.word;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchWordInsertionService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordService wordService;

    /**
     * Recibe un Map de WordResponseDTO para añadir en la BBDD bajo un único batch de datos.
     *
     * @param wordResponseDTOMap Mapa donde la clave es la palabra y el valor es su DTO con información para la BBDD.
     * @param mappedLanguages    Mapa con la información extraída de los idiomas para evitar múltiples consultas en cada batch.
     * @return Mapa con las palabras insertadas o actualizadas como placeholders.
     */
    public Map<String, WordModel> insertBatchWordsMap(
            Map<String, WordResponseDTO> wordResponseDTOMap,
            Map<String, LanguageModel> mappedLanguages) {

        // Extraer el conjunto de palabras y obtener las que ya existen en la base de datos
        Set<String> stringWords = wordResponseDTOMap.keySet();
        Map<String, WordModel> existingWords = wordService.searchListOfWords(stringWords);

        // Filtrar palabras que deben ser insertadas
        List<WordModel> wordToInsertList = wordResponseDTOMap.values().stream()
                .filter(wordDto -> {
                    // Verificar si el idioma existe en la BBDD
                    if (!mappedLanguages.containsKey(wordDto.getLanguage())) {
                        System.out.println("Skipping word " + wordDto.getWord() +
                                " because language " + wordDto.getLanguage() + " does not exist in database");
                        return false;
                    }
                    // Verificar si la palabra ya existe y si es un placeholder
                    if (existingWords.containsKey(wordDto.getWord())) {
                        return existingWords.get(wordDto.getWord()).isPlaceholder();
                    }
                    return true;
                })
                .map(wordDto -> WordModel.builder()
                        .languageModel(mappedLanguages.get(wordDto.getLanguage()))
                        .wordLength(wordDto.getLength())
                        .word(wordDto.getWord())
                        .isPlaceholder(false)
                        .build())
                .collect(Collectors.toList());

        // Guardar nuevas palabras en la base de datos
        List<WordModel> savedWords = wordRepository.saveAll(wordToInsertList);

        // Convertir la lista guardada a un Map<String, WordModel> y devolverlo
        return savedWords.stream().collect(Collectors.toMap(WordModel::getWord, word -> word));
    }
}
