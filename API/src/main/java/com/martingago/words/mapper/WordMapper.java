package com.martingago.words.mapper;

import com.martingago.words.dto.word.WordCreationDTO;
import com.martingago.words.dto.word.WordResponseDTO;
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
    public WordResponseDTO toResponseDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordResponseDTO.builder()
                .language(wordModel.getLanguageModel().getLangCode())
                .word(wordModel.getWord())
                .length(wordModel.getWordLength())
                .definitions(wordDefinitionMapper.toDTOSet(
                        wordModel.getWordDefinitionModelSet()))
                .build();
    }

    /**
     * Recibe un wordDTO y devuelve un WordModel listo para ser insertado en la BBDD.
     * @param wordCreationDTO
     * @return
     */
    public WordModel toModel(WordCreationDTO wordCreationDTO){
        if(wordCreationDTO == null) return  null;
        return  WordModel.builder()
                .languageModel(wordCreationDTO.getLanguageModel())
                .wordLength(wordCreationDTO.getLength())
                .word(wordCreationDTO.getWord())
                .build();
    }

    /**
     * Devuelve un listado de WordDTO
     * @param wordModelList
     * @return
     */
    public Set<WordResponseDTO> toDTOList(Set<WordModel> wordModelList){
        if(wordModelList == null) return Set.of();
        return wordModelList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toSet());
    }

}
