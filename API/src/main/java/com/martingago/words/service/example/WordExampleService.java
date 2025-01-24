package com.martingago.words.service.example;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.repository.WordExampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordExampleService {

    @Autowired
    WordExampleRepository wordExampleRepository;

    public void insertExamples(Set<WordExampleModel> exampleModels){
        wordExampleRepository.saveAll(exampleModels);
    }

    public void insertExamplesForDefinitions(Set<WordDefinitionModel> definitions, Set<WordDefinitionDTO> wordDefinitionDTOSet) {
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
        insertExamples(examplesToSave);
    }
}
