package com.martingago.words.service.word;

import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchWordInsertionService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    WordService wordService;

    /**
     * Recibe un Set de wordResponseDTO para añadir en la BBDD bajo un único batch de datos.
     * @param wordResponseDTOSet Set de wordDTO con la información de las palabras para añadir en la BBDD
     * @param mappedLanguages map con la información extraída de los idiomas para evitar múltiples consultas en cada batch
     * @return
     * Esta función únicamente procesará aquellas palabras que no existan en la BBDD o aquellas que sean placeholders.
     */
    public Set<WordModel> insertBatchWordsSet(
            Set<WordResponseDTO> wordResponseDTOSet,
            Map<String, LanguageModel> mappedLanguages){

        //Extrae el listado de palabras que conforman el batch y obtiene las palabras existentes.
        Set<String> stringWords = wordResponseDTOSet.stream().map(WordResponseDTO::getWord).collect(Collectors.toSet());
        Map<String, WordModel> existingWords = wordService.searchListOfWords(stringWords);


        //Realiza una serie de filtros para comprobar que palabras ya existen en la BBDD, que el ididoma sea correcto.
        List<WordModel> wordToInsertList = wordResponseDTOSet.stream()
                .filter(wordDto -> {
                    //Filtra por aquellas palabras cuyo idioma no exista en la BBDD.
                    if(!mappedLanguages.containsKey(wordDto.getLanguage())){
                        System.out.println("Skipping word " + wordDto.getWord() +  " because language " + wordDto.getLanguage()  + " does not exist in database");
                        return false;
                    }
                    //Filtra aquellas palabras que ya existen en la base de datos y comprueba si son placeholders:
                    if(existingWords.containsKey(wordDto.getWord())){
                        WordModel foundedWord = existingWords.get(wordDto.getWord());
                        return foundedWord.isPlaceholder(); //Filtra únicamente los placeholders
                    }
                    return true;
                })
                .map(wordDto -> WordModel.builder()
                        .languageModel(mappedLanguages.get(wordDto.getLanguage()))
                        .wordLength(wordDto.getLength())
                        .word(wordDto.getWord())
                        .isPlaceholder(false)
                        .build()

                )
                .collect(Collectors.toList());

        //Guarda las nuevas palabras y actualiza los placeholders. Se devuelven únicamente las palabras añadidas/placeholders
        Set<WordModel> savedWords = new HashSet<>(wordRepository.saveAll(wordToInsertList));

        return savedWords;
    }

//    private Set<WordListDefinitionsPojo> getWordDefinitionPojoList(
//            Set<WordModel> wordsToExtractDefinitions,
//            Set<WordResponseDTO> wordResponseDTOSet
//    ){
//
//    }


}
