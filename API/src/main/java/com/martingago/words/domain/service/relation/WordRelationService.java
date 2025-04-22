package com.martingago.words.domain.service.relation;

import com.martingago.words.domain.model.*;
import com.martingago.words.domain.repository.models.WordRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class WordRelationService {

    private final WordRelationRepository wordRelationRepository;

    /**
     * Obtiene un list de las palabras que tienen relación de sinónimo/antónimo con una palabra recibida como parámetro
     * @param word palabra de la que se quieren buscar relaciones
     * @param relationType tipo de relación de la palabra
     * @param langCode código de idioma de la palabra
     * @return Listado de strings de palabras que tienen relación con la palabra pasada como parámetro
     */
    public List<String> getRelationTypeByWord(String word, RelationEnumType relationType, String langCode){
        return wordRelationRepository.findWordsRelatedByWord(word, relationType, langCode);
    }

}
