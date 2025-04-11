package com.martingago.words.domain.service.example;

import com.martingago.words.domain.model.WordDefinitionModel;
import com.martingago.words.domain.model.WordExampleModel;
import com.martingago.words.dto.WordDefinitionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CreateExampleService {

    /**
     * Añade a la base de datos los ejemplos existentes en un wordDefinitionDTO asociados a una entidad WordDefinitionModel
     * @param defDto WordDefinitionDTO sobre el que se van a extraer el listado de ejemplos para ser añadidos a la BBDD
     * @param wordDefinitionModel al que están asociados los ejemplos que se van a añadir a la base de datos.
     */
    public void processExamples(WordDefinitionDTO defDto, WordDefinitionModel wordDefinitionModel) {
        Set<WordExampleModel> examples = new HashSet<>();
        if (defDto.getExamples() != null && !defDto.getExamples().isEmpty()) {
            for (String ex : defDto.getExamples()) {
                WordExampleModel example = new WordExampleModel();
                example.setExample(ex);
                example.setWordDefinitionModel(wordDefinitionModel);
                examples.add(example);
            }
        }
        //Establece en la definición, el conjunto de ejemplos existentes.
        wordDefinitionModel.setWordExampleModelSet(examples);
    }
}
