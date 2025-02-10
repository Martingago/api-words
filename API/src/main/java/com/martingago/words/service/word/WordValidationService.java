package com.martingago.words.service.word;

import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.martingago.words.utils.BatchUtils.processInBatches;

@Service
public class WordValidationService {

    @Autowired
    WordRepository wordRepository;

    /**
     * Procesa un listado de palabras y comprueba si existe o no en la Base de datos.
     * @param words
     * @return
     */
    public Set<String[]> processWordsInBatches(Set<String> words) {
        Set<String[]> results = new HashSet<>();
        int batchSize = 50; // Tamaño del lote

        // Convertir todas las palabras a minúsculas
        Set<String> lowercaseWords = new HashSet<>();
        for (String word : words) {
            lowercaseWords.add(word.toLowerCase());
        }

        processInBatches(lowercaseWords, batchSize, batch -> {
            // Buscar las palabras en la base de datos
            Set<WordModel> existingWords = wordRepository.findByWordIn(batch);

            // Crear un conjunto de palabras existentes para facilitar la búsqueda
            Set<String> existingWordSet = new HashSet<>();
            for (WordModel wordModel : existingWords) {
                existingWordSet.add(wordModel.getWord());
            }

            // Procesar cada palabra en el lote actual
            for (String word : batch) {
                boolean exists = existingWordSet.contains(word);
                results.add(new String[]{word, String.valueOf(exists)});
            }
        });

        return results;
    }

}
