package com.martingago.words.service;

import com.martingago.words.mapper.word.WordMapper;
import com.martingago.words.dto.WordResponseDTO;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class WordService {

    private final WordRepository wordRepository;

    @Autowired
    public WordService(WordRepository wordRepository){
        this.wordRepository = wordRepository;
    }

    public WordResponseDTO generateRandomWord(){
        WordModel wordModel= wordRepository.findRandomWord();
        return WordMapper.toDTO(wordModel);
    }

    public WordResponseDTO addNewWord(WordResponseDTO wordResponseDTO){
        if(wordRepository.existsByWord(wordResponseDTO.getWord())){
            throw new DuplicateKeyException(wordResponseDTO.getWord());
        }
        try{
            WordModel wordModel = WordMapper.toModel(wordResponseDTO);
            WordModel wordSaved = wordRepository.save(wordModel);
            return WordMapper.toDTO(wordSaved);
        }catch (Exception e){
            throw  new RuntimeException("Error");
        }
    }
}
