package com.martingago.words.mapper;

import com.martingago.words.dto.WordDTO;
import com.martingago.words.model.WordModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
        return new WordDTO(
                wordModel.getLanguageModel().getLang_code(),
                wordModel.getWord(),
                wordModel.getWordLength(),
                wordDefinitionMapper.toDTOList(wordModel.getWordDefinitionModelList())
        );
    }

    /**
     * Devuelve un listado de WordDTO
     * @param wordModelList
     * @return
     */
    public List<WordDTO> toDTOList(List<WordModel> wordModelList){
        if(wordModelList == null) return List.of();
        return wordModelList.stream().map(this::toDTO).collect(Collectors.toList());
    }

}
