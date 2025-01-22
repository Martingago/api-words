package com.martingago.words.mapper;

import com.martingago.words.dto.WordDTO;
import com.martingago.words.model.WordModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WordMapper {

    @Autowired
    WordDefinitionMapper wordDefinitionMapper;

    /**
     * Genera un WordDTO a partir de un WordModel
     * → convierte un LanguageModel a un languageDTO
     * → convierte un List<WordDefinitionModel> en un list<WordDefinitionDTO>
     * @param wordModel
     * @return
     */
    public WordDTO toDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordDTO.builder()
                .language(wordModel.getLanguageModel().getLangCode())
                .word(wordModel.getWord())
                .length(wordModel.getWordLength())
                .definitions(wordDefinitionMapper.toDTOSet(
                        wordModel.getWordDefinitionModelSet()))
                .build();
    }

    public WordDTO toWordDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordDTO.builder()
                .languageModel(wordModel.getLanguageModel())
                .length(wordModel.getWordLength())
                .word(wordModel.getWord())
                .build();
    }
    
    /**
     * Recibe un wordDTO y devuelve un WordModel listo para ser insertado en la BBDD.
     * @param wordDTO
     * @return
     */
    public WordModel toModel(WordDTO wordDTO){
        if(wordDTO == null) return  null;
        return  WordModel.builder()
                .languageModel(wordDTO.getLanguageModel())
                .wordLength(wordDTO.getLength())
                .word(wordDTO.getWord())
                .build();
    }

    /**
     * Devuelve un listado de WordDTO
     * @param wordModelList
     * @return
     */
    public Set<WordDTO> toDTOList(Set<WordModel> wordModelList){
        if(wordModelList == null) return Set.of();
        return wordModelList.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

}
