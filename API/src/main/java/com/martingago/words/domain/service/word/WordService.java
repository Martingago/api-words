package com.martingago.words.domain.service.word;

import com.martingago.words.context.WordValidator;
import com.martingago.words.dto.word.request.WordBatchDTO;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;


    /**
     * Guarda un wordModel en la base de datos y devuelve el objeto persistido.
     * @param wordModel
     * @return
     */
    public WordModel saveWordModel(WordModel wordModel){
        return wordRepository.save(wordModel);
    }


    /**
     * Busca una palabra en la base de datos.
     * @param word palabra que se quiere buscar
     * @return WordResponseViewDTO
     */
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
    public WordResponseViewDTO getRandomWord(Integer wordLength){
        Long id = wordRepository.findRandomWordId(wordLength);
        WordModel wordModel = wordRepository.findWordById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Couldn't find a word with those requirements"));
        return  wordMapper.toResponseDTO(wordModel);
    }


    /**
     * Busca una palabra en la base de datos teniendo especificado un idioma.
     * @param word palabra que se quiere buscar en la base de datos.
     * @param langCode código de idioma de la palabra
     * @return WordModel
     */
    public WordModel searchBasicWordWithLanguage(String word, String langCode){
        return wordRepository.findByWordAndLanguage(word, langCode).orElseThrow(() ->
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
     * Recibe un WordBatchDTO, extrae string de palabras con las que tiene relación, y las busca en la base de datos.
     * Devuelve un map de WordBatchReferenceDTO con la información encontrada.
     * @param wordBatchDTO objeto sobre el que se quiere realizar la búsqueda.
     * @return
     */
    public Map<String, WordBatchReferenceDTO> findReferencesFromWordDTO(WordBatchDTO wordBatchDTO){
        Map<String, WordBatchReferenceDTO> wordReferenceMap = new HashMap<>();
        Set<String> wordsToFetch = new HashSet<>();

        // Extraemos las palabras, sinónimos y antónimos
        wordsToFetch.add(wordBatchDTO.getWord());
        wordBatchDTO.getDefinitions().forEach(definition -> {
            if (definition.getSynonyms() != null) wordsToFetch.addAll(definition.getSynonyms());
            if (definition.getAntonyms() != null) wordsToFetch.addAll(definition.getAntonyms());
        });

        if (!wordsToFetch.isEmpty()) {
            List<WordBatchReferenceDTO> existingBatchRefs = wordRepository.findReferencesByWordIn(wordsToFetch);

            wordReferenceMap = existingBatchRefs.stream()
                    .collect(Collectors.toMap(WordBatchReferenceDTO::getWord, ref -> ref));
        }
        return wordReferenceMap;
    }

}
