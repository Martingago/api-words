package com.martingago.words.service.batchInsertion;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.word.BatchWordInsertionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BatchProcessingInsertionService {

    private static final int BATCH_SIZE = 500;

    @Autowired
    BatchWordInsertionService batchWordInsertionService;

    @Transactional
    public void processJsonFile(Set<WordResponseDTO> allWords) {
        int totalWords = allWords.size();
        int processedWords = 0;

        // Procesar en lotes
        List<WordResponseDTO> wordsList = new ArrayList<>(allWords);
        for (int i = 0; i < wordsList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, wordsList.size());
            Set<WordResponseDTO> batch = new HashSet<>(wordsList.subList(i, end));

            try {
                Set<WordModel> insertedWords = batchWordInsertionService.insertBatchWordsSet(batch);
                processedWords += insertedWords.size();
                log.info("Processed batch {}/{}: {} words inserted",
                        (i/BATCH_SIZE + 1),
                        (totalWords + BATCH_SIZE - 1)/BATCH_SIZE,
                        insertedWords.size());
            } catch (Exception e) {
                log.error("Error processing batch starting at index {}: {}", i, e.getMessage());
                // Aquí podrías implementar lógica de reintento o manejo de errores
            }
        }

        log.info("Finished processing {} words out of {}", processedWords, totalWords);
    }

}
