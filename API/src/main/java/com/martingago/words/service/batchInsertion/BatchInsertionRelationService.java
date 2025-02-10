package com.martingago.words.service.batchInsertion;


import com.martingago.words.POJO.DefinitionEstructurePojo;
import com.martingago.words.POJO.WordPojo;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordRelationModel;
import com.martingago.words.repository.WordRelationRepository;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class BatchInsertionRelationService {

    @Autowired
    BatchWordInsertionService batchWordInsertionService;

    @Autowired
    WordRelationRepository wordRelationRepository;

    public void insertBatchRelationSet(
            Set<DefinitionEstructurePojo> setDefinitionPojoToInsert) {

        Set<WordRelationModel> insertedWordRelationModelSet = new HashSet<>();

        BatchUtils.processInBatches(setDefinitionPojoToInsert, 50, batch -> {
            try {
                // Usamos un Map para evitar palabras duplicadas
                Map<String, WordPojo> uniqueWordsMap = new HashMap<>();

                batch.forEach(pojo -> pojo.getRelationPojoList().forEach(relationPojo -> {
                    String word = relationPojo.getWord();
                    if (!uniqueWordsMap.containsKey(word)) {
                        uniqueWordsMap.put(word, WordPojo.builder()
                                .word(word)
                                .languageModel(pojo.getWordDefinitionModel().getWord().getLanguageModel())
                                .build());
                    }
                }));

                // Convertimos el Map a un Set
                Set<WordPojo> relatedWords = new HashSet<>(uniqueWordsMap.values());
                Map<String, WordModel> wordModelMap = batchWordInsertionService.insertBatchPlaceholderWords(relatedWords);

                // Crea las relaciones WordRelationModel
                List<WordRelationModel> relationsToInsert = batch.stream()
                        .flatMap(pojo -> pojo.getRelationPojoList().stream()
                                .map(relationPojo -> WordRelationModel.builder()
                                        .wordDefinitionModel(pojo.getWordDefinitionModel()) // FK de la definición
                                        .wordRelated(wordModelMap.get(relationPojo.getWord())) // FK de la palabra relacionada
                                        .relationEnumType(relationPojo.getRelationEnumType()) // Tipo de relación
                                        .build()))
                        .toList();

                // Inserta las relaciones en batch
                wordRelationRepository.saveAll(relationsToInsert);
            } catch (Exception e) {
                log.error("Error processing word relations batch: {}", e.getMessage(), e);
            }
        });
    }

}
