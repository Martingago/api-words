package com.martingago.words.utils;

import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.model.WordModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WordListDefinitionUtils {

    /**
     * Funcion que recibe un map de palabras que fueron añadidas + total wordResponseDTO de elementos a añadir y devueve
     * un map de objetos WordListDefinitionsPojo
     * @param wordModelMap
     * @param wordResponseDTOMap
     * @return
     */
    public Map<String, WordListDefinitionsPojo> getCommonWordsWithDefinitions(
            Map<String, WordModel> wordModelMap,
            Map<String, WordResponseViewDTO> wordResponseDTOMap) {

        return wordModelMap.entrySet().stream()
                // Filtrar solo las palabras que existen en ambos mapas
                .filter(entry -> wordResponseDTOMap.containsKey(entry.getKey()))
                // Convertir cada entrada en un objeto WordListDefinitionsPojo
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Clave del mapa
                        entry -> {
                            String word = entry.getKey();
                            WordModel wordModel = entry.getValue();
                            Set<WordDefinitionDTO> definitions = wordResponseDTOMap.get(word).getDefinitions();

                            return WordListDefinitionsPojo.builder()
                                    .wordModel(wordModel)
                                    .wordDefinitionDTOSet(definitions)
                                    .build();
                        }
                ));
    }


}
