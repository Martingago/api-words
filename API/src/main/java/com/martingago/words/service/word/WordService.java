package com.martingago.words.service.word;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordMapper wordMapper;


    public WordResponseDTO getWordByName(String word){
        WordModel wordModel = wordRepository.findByWordWithRelations(word)
                .orElseThrow( () ->
                        new EntityNotFoundException("Word " + word + " was not founded on database"));
        return wordMapper.toResponseDTO(wordModel);
    }

    public WordResponseDTO getRandomWord(){
        WordModel wordModel = wordRepository.findRandomWord()
                .orElseThrow(() ->
                        new EntityNotFoundException("Word with language: " + " was not founded"));
        return  wordMapper.toResponseDTO(wordModel);
    }

    public WordModel searchBasicWord(String word){
        return  wordRepository.findByWord(word)
                .orElseThrow(() ->
                        new EntityNotFoundException("Word " + word + " was not founded on database"));
    }

    /**
     * Función que recibe un listado de Strings de palabras que deben ser insertadas en la BBDD como placeholders
     * @param wordsStringSet Set de Strings de palabras para añadir en la BBDD como placeholders.
     * @param languageModel idioma en el que se tienen que crear los placeholders.
     * @return Set de WordModel con las palabras (placeholders) que han sido añadidos a la BBDD.
     */
    public Set<WordModel> insertPlaceholderWordsFromList(Set<String> wordsStringSet, LanguageModel languageModel){
        Set<WordModel> placeholdersToInsert = wordsStringSet.stream().map(
                placeholder -> WordModel.builder()
                        .isPlaceholder(true)
                        .word(placeholder)
                        .wordLength(placeholder.length())
                        .languageModel(languageModel)
                        .build()
        ).collect(Collectors.toSet());
        return new HashSet<>(wordRepository.saveAll(placeholdersToInsert));
    }



}
