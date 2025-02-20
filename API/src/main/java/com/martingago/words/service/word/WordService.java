package com.martingago.words.service.word;

import com.martingago.words.POJO.WordValidator;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordMapper wordMapper;


    public WordResponseViewDTO getWordByName(String word){
        WordModel wordModel = wordRepository.findByWordWithRelations(word)
                .orElseThrow( () ->
                        new EntityNotFoundException("Word " + word + " was not founded on database"));
        return wordMapper.toResponseDTO(wordModel);
    }

    /**
     * Obtiene una palabra aleatoria de la base de datos bajo un código de idioma establecido.
     * @return
     */
    public WordResponseViewDTO getRandomWord(String langCode){
        WordModel wordModel = wordRepository.findRandomWord(langCode)
                .orElseThrow(() ->
                        new EntityNotFoundException("Word with language: " + " was not founded"));
        return  wordMapper.toResponseDTO(wordModel);
    }


    /**
     * Busca una palabra en la base de datos
     * @param word
     * @return
     */
    public WordModel searchBasicWord(String word){
        return  wordRepository.findByWord(word)
                .orElseThrow(() ->
                        new EntityNotFoundException("Word " + word + " was not founded on database"));
    }

    public WordModel searchBasicWordWithLanguage(String word, String langCode){
        return wordRepository.findByWordWithRelationsByLanguage(word, langCode).orElseThrow(() ->
                new EntityNotFoundException("Word '" + word + "' with language: '" + langCode + "' was not founded on database"));
    }

    /**
     * Comprueba que una palabra pasada como parámetro exista o no en la base de datos.
     * Si la palabra existe, se comprueba si es un placeholder o no.
     * @param word palabra que se quiere comprobar si existe o no en la BBDD.
     * @return true: La palabra existe en la BBDD y no es un placeholder,
     * false: La palabra no existe, o en caso de existir sea un placeholder
     */
    public WordValidator isWordLocatedAndNotPlaceholder(String word){
        Optional<WordModel> wordModel = wordRepository.findByWordWithRelations(word);
        if(wordModel.isPresent()){
            return WordValidator.builder()
                    .exists(!wordModel.get().isPlaceholder())
                    .wordModel(wordModel.get())
                    .build();
        }
        return new WordValidator(false, null);
    }

    /**
     * Elimina una palabra de la base de datos
     * @param wordModel
     */
    public void deleteWordByWordModel(WordModel wordModel){
        wordRepository.delete(wordModel);
    }

    /**
     * Busca un set de strings de palabras en la BBDD y obtiene un map [word ⇾ WordModel] con los resultados encontrados.
     * @param wordsStringSet Set de Strings de palabras a buscar en la BBDD
     * @return Map<String, WordModel> con las palabras encontradas en la BBDD
     */
    public Map<String, WordModel> searchListOfWords(Set<String> wordsStringSet){
        Set<WordModel> wordModelSet = wordRepository.findByWordIn(wordsStringSet);
        return wordModelSet.stream().collect(Collectors.toMap(WordModel::getWord, word -> word, (existing, duplicate) -> existing));
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
