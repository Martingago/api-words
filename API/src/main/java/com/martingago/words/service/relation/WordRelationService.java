package com.martingago.words.service.relation;

import com.martingago.words.POJO.DefinitionRelation;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.*;
import com.martingago.words.repository.WordRelationRepository;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.word.WordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordRelationService {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordService wordService;

    @Autowired
    WordRelationRepository wordRelationRepository;

    /**
     * Función que inserta en un Set de DefinitionModels las relaciones (SINÓNIMA/ANTÓNIMA) que recibe de un Set de WordDefinitionDTO
     * @param wordDefinitionModelSet > Set de definiciones sobre el que se establece el tipo de relación (ANTÓNIMA/SINÓNIMA)
     * @param wordDefinitionDTOSet > DTO que se recibe y del que se extráe la información de las definiciones para crear las relaciones
     * @param languageModel > Idioma de preferencia para crear aquellos word placeholders referenciados en las relaciones.
     */
    public Set<WordRelationModel> insertRelationsToDefinitions(Set<WordDefinitionModel> wordDefinitionModelSet,
                                             Set<WordDefinitionDTO> wordDefinitionDTOSet,
                                             LanguageModel languageModel){
        //Map con las palabras y su relación con la definición y tipo de relación:
        Map<String, DefinitionRelation> relatedWordsMap = new HashMap<>();

        //Obtener el listado total de palabras con las que existe una relación:
        for(WordDefinitionDTO definitionDTO : wordDefinitionDTOSet){
            WordDefinitionModel definition = wordDefinitionModelSet.stream()
                    .filter(def -> def.getWordDefinition().equals(definitionDTO.getDefinition()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Def not founded: " + definitionDTO.getDefinition()));

            // Asegurarse de que los conjuntos no sean null antes de iterar
            Set<String> synonyms = definitionDTO.getSynonyms() != null ? definitionDTO.getSynonyms() : Collections.emptySet();
            Set<String> antonyms = definitionDTO.getAntonyms() != null ? definitionDTO.getAntonyms() : Collections.emptySet();

            //Por cada definicion añadir las SINÓNIMAS Y ANTÓNIMAS en el relatedWordsMap:
            synonyms.forEach(synonym ->
                    relatedWordsMap.put(synonym, DefinitionRelation.builder()
                            .relationEnumType(RelationEnumType.SINONIMA)
                            .wordDefinitionModel(definition)
                            .build())
            );

            antonyms.forEach(antonym ->
                    relatedWordsMap.put(antonym, DefinitionRelation.builder()
                            .relationEnumType(RelationEnumType.ANTONIMA)
                            .wordDefinitionModel(definition)
                            .build())
            );
        }

        // 2. Obtener el listado total de palabras relacionadas
        Set<String> relatedWordsStrings = relatedWordsMap.keySet();

        Set<WordModel> existingWords = wordRepository.findByWordIn(relatedWordsStrings);

        //Del listado total, filtrar las que ya existen y quedarse con las que no se han encontrado:
        Set<String> existingWordNames = existingWords.stream()
                .map(WordModel::getWord)
                .collect(Collectors.toSet());

        Set<String> missingWords = relatedWordsStrings.stream()
                .filter(word -> !existingWordNames.contains(word))
                .collect(Collectors.toSet());

        // 5. Insertar las palabras faltantes como placeholders
        Set<WordModel> placeholderWords = wordService.insertPlaceholderWordsFromList(missingWords, languageModel);

        // 6. Crear un mapa de todas las palabras (existentes + placeholders)
        Map<String, WordModel> wordMap = new HashMap<>();
        existingWords.forEach(word -> wordMap.put(word.getWord(), word));
        placeholderWords.forEach(word -> wordMap.put(word.getWord(), word));

        Set<WordRelationModel> relationToSaveSet = new HashSet<>();
        relatedWordsMap.forEach((word, definitionRelation) -> {
            WordModel wordRelated = wordMap.get(word);

            //Se genera la relación extrayendo los datos del map: relatedWordsMap
            WordRelationModel relation = WordRelationModel.builder()
                    .wordRelated(wordRelated)
                    .wordDefinitionModel(definitionRelation.getWordDefinitionModel())
                    .relationEnumType(definitionRelation.getRelationEnumType())
                    .build();
            relationToSaveSet.add(relation); //Se añade la nueva relación al set de relaciones para guardar
        });
        //Guarda todas las relaciones de las palabras
        return new HashSet<>(wordRelationRepository.saveAll(relationToSaveSet));
    }



}
