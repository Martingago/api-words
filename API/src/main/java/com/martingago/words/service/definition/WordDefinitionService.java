package com.martingago.words.service.definition;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.*;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.service.example.WordExampleService;
import com.martingago.words.service.qualification.WordQualificationService;
import com.martingago.words.service.relation.WordRelationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordDefinitionService {

    @Autowired
    WordDefinitionRepository wordDefinitionRepository;

    @Autowired
    WordQualificationService wordQualificationService;

    @Autowired
    WordExampleService wordExampleService;

    @Autowired
    WordRelationService wordRelationService;

    /**
     * Función que inserta las definiciones de una palabra WordModel pasada como parámetro.
     * @param wordModel de la palabra sobre la que se quieren realizar inserciones de definiciones.
     * @param wordDefinitionDTOSet Set que contiene WordDefinitionDTO con la información a ingresar en la BBDD
     * @param languageModel idioma de referencia de la palabra para usar en la creación de posibles placeholders
     * @return Set de WordDefinitionModel que contiene la información de las definiciones de la palabra que han sido añadidos a la BBDD.
     */
    @Transactional
    public Set<WordDefinitionModel> validateAndInsertDefinitions(WordModel wordModel,
                                                                 Set<WordDefinitionDTO> wordDefinitionDTOSet,
                                                                 LanguageModel languageModel) {
        // Extraer las qualifications de las definiciones recibidas
        Set<String> qualifications = wordDefinitionDTOSet.stream()
                .map(WordDefinitionDTO::getQualification)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Validar e insertar las qualifications
        Map<String, WordQualificationModel> qualificationModelMap = wordQualificationService.validateAndInsertQualifications(qualifications);

        // Crear las definiciones y asociar las qualifications
        Set<WordDefinitionModel> definitionsToSave = wordDefinitionDTOSet.stream()
                .map(definitionDTO -> WordDefinitionModel.builder()
                        .wordDefinition(definitionDTO.getDefinition())
                        .word(wordModel)
                        .wordQualificationModel(qualificationModelMap.get(definitionDTO.getQualification()))
                        .build())
                .collect(Collectors.toSet());

        // Guardar las definiciones en la base de datos y convertir a Set
        Set<WordDefinitionModel> savedDefinitions =new HashSet<>(wordDefinitionRepository.saveAll(definitionsToSave));

        //Crear los ejemplos de las definiciones
        Set<WordExampleModel> wordExampleModelSet=  wordExampleService.insertExamplesForDefinitions(savedDefinitions, wordDefinitionDTOSet);

        //Insertar las relaciones con otras palabras.
        Set<WordRelationModel> wordRelationModelSet = wordRelationService.insertRelationsToDefinitions(savedDefinitions, wordDefinitionDTOSet, languageModel);

        return  savedDefinitions;
    }


}
