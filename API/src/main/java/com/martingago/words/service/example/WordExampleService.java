package com.martingago.words.service.example;

import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.repository.WordExampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WordExampleService {

    @Autowired
    WordExampleRepository wordExampleRepository;

    public void insertExamples(Set<WordExampleModel> exampleModels){
        wordExampleRepository.saveAll(exampleModels);
    }
}
