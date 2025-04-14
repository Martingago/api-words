package com.martingago.words.mapper.models;

import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.domain.model.WordModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WordMapper {

    private final WordDefinitionMapper wordDefinitionMapper;

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
                .length(wordModel.getLength())
                .definitions(wordDefinitionMapper.toDTOSet(
                        wordModel.getWordDefinitionModelSet()))
                .build();
    }

    /**
     * Convierte un WordModel en un objeto WordBatchReferenceDTO
     * @param wordModel entidad de WordModel que se quiere procesar y convertir
     * @return WordReferenceDTO objeto Serializable con la información principal de una palabra.
     */
    public WordBatchReferenceDTO toWordBatchReferenceDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordBatchReferenceDTO.builder()
                .id(wordModel.getId())
                .word(wordModel.getWord())
                .isPlaceholder(wordModel.isPlaceholder())
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
