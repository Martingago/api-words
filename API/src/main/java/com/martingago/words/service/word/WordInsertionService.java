package com.martingago.words.service.word;

import com.martingago.words.dto.WordDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.language.LanguageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;

public class WordInsertionService {

    @Autowired
    LanguageService languageService;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordMapper wordMapper;


    private WordModel searchBasicWord(String word){
        return  wordRepository.findByWord(word)
                .orElseThrow(() ->
                        new EntityNotFoundException("Word " + word + " was not founded on database"));
    }

    /**
     * Inserta una word en la tabla de Words de la aplicación.
     * @param wordDTO
     * @return
     */
    private WordModel insertBasicWord(WordDTO wordDTO){
        WordModel wordModel = wordMapper.toModel(wordDTO);
        return wordRepository.save(wordModel);
    }


    private void insertFullWord(WordDTO wordDTO){
        //Compruebo si el idioma de la palabra existe:
        LanguageModel languageModel;
        try{
            languageModel= languageService.searchLanguageByLangCode(wordDTO.getLanguage());
        }catch (EntityNotFoundException e){
            throw  e;
        }

        //Comprobar qualification

        WordModel wordModel = WordModel.builder()
                .word(wordDTO.getWord())
                .wordLength(wordDTO.getLength())
                .languageModel(languageModel)
                .build();

        wordRepository.save(wordModel);

    }

}
