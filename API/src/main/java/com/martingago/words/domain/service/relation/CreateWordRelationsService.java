package com.martingago.words.domain.service.relation;

import com.martingago.words.domain.model.*;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
/**
 * Clase que contiene los métodos necesarios para crear relaciones entre Definiciones y palabras.
 */
public class CreateWordRelationsService {

    private final EntityManager entityManager;

    /**
     * Procesa las relaciones de sinónimos de una definición.
     * @param defDto WordDefinitionDTO sobre el que se quieren realizar las relaciones
     * @param wordDefinitionModel Modelo con el que se van a asociar las relaciones
     * @param newWordsModelToPersist Palabras que se van a persistir en la Base de datos.
     * @param existingDBWordsMap palabras que existen en la base de datos.
     */
    public void processSynonyms(WordDefinitionDTO defDto,
                                 WordDefinitionModel wordDefinitionModel,
                                 Map<String, WordModel> newWordsModelToPersist,
                                 Map<String, WordBatchReferenceDTO> existingDBWordsMap
                                 ) {
        if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
            //Obtiene el listado de entidades con las que tiene relación de sinónimo.
            Set<WordModel> synonymWords = processRelatedWords(
                    defDto.getSynonyms(),
                    wordDefinitionModel.getWord().getLanguageModel(),
                    newWordsModelToPersist,
                    existingDBWordsMap);

            Set<WordRelationModel> synonymRelations = createWordRelations(wordDefinitionModel, synonymWords, RelationEnumType.SINONIMA);
            wordDefinitionModel.setWordRelationModelSet(synonymRelations);
        }
    }

    /**
     * Procesa las relaciones de antónimos de una definición.
     * @param defDto WordDefinitionDTO sobre el que se quieren realizar las relaciones
     * @param wordDefinitionModel Modelo con el que se van a asociar las relaciones
     * @param newWordsModelToPersist Palabras que se van a persistir en la Base de datos.
     * @param existingDBWordsMap palabras que existen en la base de datos.
     */
    public void processAntonyms(WordDefinitionDTO defDto,
                                 WordDefinitionModel wordDefinitionModel,
                                 Map<String, WordModel> newWordsModelToPersist,
                                 Map<String, WordBatchReferenceDTO> existingDBWordsMap
    ) {
        if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
            Set<WordModel> antonymWords = processRelatedWords(
                    defDto.getSynonyms(),
                    wordDefinitionModel.getWord().getLanguageModel(),
                    newWordsModelToPersist,
                    existingDBWordsMap);
            Set<WordRelationModel> antonymRelations = createWordRelations(wordDefinitionModel, antonymWords, RelationEnumType.ANTONIMA);
            wordDefinitionModel.setWordRelationModelSet(antonymRelations);
        }
    }



    /**
     * Función que se encarga de obtener los modelos de palabras (WordModel) de aquellas palabras que se encuentran
     * relacionadas con la palabra principal
     * @param relatedWords palabas relacionadas a buscar en la Base de datos.
     * @param language idioma (Usado para crear los placeholders)
     * @param newWordsModelToPersist map de palabras que se quieren persistir en la base de datos (placeholders)
     * @param existingDBWordsMap listado de palabras existentes en la Base de datos
     * @return Set de Models de palabras con la que existe relación
     */
    private Set<WordModel> processRelatedWords(Set<String> relatedWords,
                                               LanguageModel language,
                                               Map<String, WordModel> newWordsModelToPersist,
                                               Map<String, WordBatchReferenceDTO> existingDBWordsMap) {

        Set<WordModel> relatedWordEntities = new HashSet<>();

        for (String related : relatedWords) {
            //Si la entidad existe en palabras que van a ser persistidas se obtiene su referencia
            WordModel relatedWord = Optional.ofNullable(newWordsModelToPersist.get(related))
                    .orElseGet(() -> {
                        //Si la entiad existe en la base de datos, se obtiene su referencia
                        WordBatchReferenceDTO ref = existingDBWordsMap.get(related);
                        if (ref != null) {
                            return entityManager.getReference(WordModel.class, ref.getId());
                        } else {
                            //Si no existe en ninguna lista, se crea un placeholder de palabra.
                            WordModel placeholder = new WordModel();
                            placeholder.setWord(related);
                            placeholder.setLength(related.length());
                            placeholder.setLanguageModel(language);
                            placeholder.setPlaceholder(true);
                            newWordsModelToPersist.put(related, placeholder);
                            return placeholder;
                        }
                    });

            relatedWordEntities.add(relatedWord);
        }

        return relatedWordEntities;
    }


    /**
     * Función que crea la relación entre una definicion y un conjunto de palabras.
     * @param definition WordDefinitionModel con el que se establece la relación de las palabras
     * @param relatedWords Lista de palabras (models) que tienen relación con la definición (WordDefinitionModel)
     * @param type ENUM tipo de relación (ANTÓNIMA/ SINÓNIMA)
     * @return Set de wordRelationModels que se quieren crear en la base de datos.
     */
    private Set<WordRelationModel> createWordRelations(WordDefinitionModel definition, Set<WordModel> relatedWords, RelationEnumType type) {
        Set<WordRelationModel> relations = new HashSet<>();
        for (WordModel relatedWord : relatedWords) {
            WordRelationModel relation = new WordRelationModel();
            relation.setWordDefinitionModel(definition);
            relation.setWordRelated(relatedWord);
            relation.setRelationEnumType(type);
            relations.add(relation);
        }
        return relations;
    }
}
