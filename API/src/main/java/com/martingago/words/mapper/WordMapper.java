package com.martingago.words.mapper;

import com.martingago.words.dto.word.response.WordResponseViewDTO;
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
    public WordResponseViewDTO toResponseDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordResponseViewDTO.builder()
                .language(wordModel.getLanguageModel().getLangCode())
                .word(wordModel.getWord())
                .length(wordModel.getWordLength())
                .definitions(wordDefinitionMapper.toDTOSet(
                        wordModel.getWordDefinitionModelSet()))
                .build();
    }

    /**
     * Devuelve un listado de WordDTO
     * @param wordModelList
     * @return
     */
    public Set<WordResponseViewDTO> toDTOList(Set<WordModel> wordModelList){
        if(wordModelList == null) return Set.of();
        return wordModelList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toSet());
    }

}
