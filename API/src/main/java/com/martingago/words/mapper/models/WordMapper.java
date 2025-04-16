package com.martingago.words.mapper.models;

import com.martingago.words.dto.models.word.request.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.response.WordDTO;
import com.martingago.words.domain.model.WordModel;
import lombok.RequiredArgsConstructor;
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
    public WordDTO toResponseDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return WordDTO.builder()
                .language(wordModel.getLanguageModel().getLangCode())
                .word(wordModel.getWord())
                .length(wordModel.getLength())
                .definitions(wordDefinitionMapper.toDTOSet(
                        wordModel.getWordDefinitionModelSet()))
                .build();
    }

    /**
     * Convierte un WordModel en un objeto SimpleWordSerializableDTO
     * @param wordModel entidad de WordModel que se quiere procesar y convertir
     * @return WordReferenceDTO objeto Serializable con la información principal de una palabra.
     */
    public SimpleWordSerializableDTO toWordBatchReferenceDTO(WordModel wordModel){
        if(wordModel == null) return null;
        return SimpleWordSerializableDTO.builder()
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
    public Set<WordDTO> toDTOList(Set<WordModel> wordModelList){
        if(wordModelList == null) return Set.of();
        return wordModelList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toSet());
    }

}
