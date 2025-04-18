package com.martingago.words.mapper.microservices;

import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.models.word.WordDTO;
import org.springframework.stereotype.Component;

@Component
public class ExternalWordMapper {

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
