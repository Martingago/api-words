package com.martingago.words.domain.service.word;

import com.martingago.words.domain.repository.models.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckWordsInBatchesService {

    private final WordRepository wordRepository;

    /**
     * Recibe un listado de palabras y realiza una busqueda por lotes en la base de datos.
     * @param words Set de palabras a buscar en la base de datos.
     * @param batchSize tamaño del batch (lotes individuales)
     * @return Map con la información de la palabra + boolean si existe o no en la BBDD.
     */
    public Map<String, Boolean> checkWordsInBatches(Set<String> words, int batchSize) {
        Map<String, Boolean> result = new HashMap<>();
        List<String> wordList = new ArrayList<>(words);

        for (int i = 0; i < wordList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, wordList.size());
            Set<String> batch = new HashSet<>(wordList.subList(i, end));
            //Busca en la BBDD en lotes las palabras indicadas.
            
            Set<String> existingWords = wordRepository.findWordsByWordIn(batch);
            Set<String> existingWordsSet = new HashSet<>(existingWords);

            for (String word : batch) {
                result.put(word, existingWordsSet.contains(word));
            }
        }
        return result;
    }


}
