package com.martingago.words.mapper.word;

import com.martingago.words.dto.WordResponseDTO;
import com.martingago.words.model.WordModel;

public class WordMapper {

    public static WordResponseDTO toDTO(WordModel wordModel){
        return WordResponseDTO.builder()
                .word(wordModel.getWord())
                .definition(wordModel.getDefinition())
                .language(wordModel.getLanguage())
                .wordLength(wordModel.getWordLength())
                .build();
    }
    public  static WordModel toModel(WordResponseDTO wordResponseDTO){
        System.out.println(wordResponseDTO);
        return WordModel.builder()
                .word(wordResponseDTO.getWord())
                .definition(wordResponseDTO.getDefinition())
                .language(wordResponseDTO.getLanguage())
                .wordLength(wordResponseDTO.getWordLength())
                .build();
    }
}
