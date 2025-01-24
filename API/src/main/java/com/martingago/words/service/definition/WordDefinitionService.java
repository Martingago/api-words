package com.martingago.words.service.definition;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.service.example.WordExampleService;
import com.martingago.words.service.qualification.WordQualificationService;
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

    @Transactional
    public Set<WordDefinitionModel> validateAndInsertDefinitions(WordModel wordModel, Set<WordDefinitionDTO> wordDefinitionDTOSet) {
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
        insertExamplesForDefinitions(savedDefinitions, wordDefinitionDTOSet);

        return  savedDefinitions;
    }


    private void insertExamplesForDefinitions(Set<WordDefinitionModel> definitions, Set<WordDefinitionDTO> wordDefinitionDTOSet) {
        // Set para agrupar todos los ejemplos
        Set<WordExampleModel> examplesToSave = new HashSet<>();

        // Recorrer las definiciones guardadas
        for (WordDefinitionModel definition : definitions) {
            // Obtener los ejemplos de la definición actual
            Set<String> examples = wordDefinitionDTOSet.stream()
                    .filter(dto -> dto.getDefinition().equals(definition.getWordDefinition()))
                    .flatMap(dto -> dto.getExamples().stream())
                    .collect(Collectors.toSet());

            // Crear los WordExampleModel y asociarlos a la definición
            examples.forEach(example -> {
                WordExampleModel wordExampleModel = WordExampleModel.builder()
                        .example(example)
                        .wordDefinitionModel(definition) // Asociar a la definición
                        .build();
                examplesToSave.add(wordExampleModel);
            });
        }

        // Guardar todos los ejemplos en una sola operación
        wordExampleService.insertExamples(examplesToSave);
    }

}
