package com.martingago.words.POJO;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter

public class WordListDefinitionsPojo {

    /**
     * Este objeto es un POJO cuya finalidad es tener un mayor control a la hora de crear Definiciones en la Base
     * de datos. Se agrupa la información de una palabra con un listado de definiciones(WordDefinitionDTO) que
     * quieren ser añadidos en la Base de datos.
     */
    WordModel wordModel;
    Set<WordDefinitionDTO> wordDefinitionDTOSet;
}
