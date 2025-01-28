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
     * Este POJO se utiliza para manejar los batch de palabras, del wordModel que ha sido insertado en el batch
     * se obtiene su listado de definiciones. Para crear un definitionModel se extrae la información de su palabra
     * asociada gracias a este objeto interno.
     */
    WordModel wordModel;
    Set<WordDefinitionDTO> wordDefinitionDTOSet;
}
