package com.martingago.words.service.word;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
