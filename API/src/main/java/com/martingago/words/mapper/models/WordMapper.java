package com.martingago.words.mapper.models;

import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.models.word.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.WordDTO;
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
     * Transforma un wordDTOExternal recibido en un microservicio, en un wordDTO usable dentro de la aplicación.
     * @param wordDTOExternal objeto recibido desde el microservicio y que se quiere convertir en un wordDTO usable dentro de la aplicación
     * @return WordDTO usable dentro de la aplicación.
     */
    public WordDTO toInternalDTO(WordDTOExternal wordDTOExternal){
        return WordDTO.builder()
                .word(wordDTOExternal.getWord())
                .length(wordDTOExternal.getLength())
                .language(wordDTOExternal.getLanguage())
                .definitions(wordDTOExternal.getDefinitions())
                .build();
    }

}
