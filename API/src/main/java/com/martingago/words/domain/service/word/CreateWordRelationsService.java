package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class CreateWordRelationsService {

    private final EntityManager entityManager;


    /**
     * Función que se encarga de obtener los modelos de palabras (WordModel) de aquellas palabras que se encuentran
     * relacionadas con la palabra principal
     * @param relatedWords palabas relacionadas a buscar en la Base de datos.
     * @param language idioma (Usado para crear los placeholders)
     * @param newWordsModelToPersist map de palabras que se quieren persistir en la base de datos (placeholders)
     * @param existingDBWordsMap listado de palabras existentes en la Base de datos
     * @return Set de Models de palabras con la que existe relación
     */
    public Set<WordModel> processRelatedWords(Set<String> relatedWords,
                                               LanguageModel language,
                                               Map<String, WordModel> newWordsModelToPersist,
                                               Map<String, WordBatchReferenceDTO> existingDBWordsMap) {

        Set<WordModel> relatedWordEntities = new HashSet<>();

        for (String related : relatedWords) {
            //Si la entidad existe en palabras que van a ser persistidas se obtiene su referencia
            WordModel relatedWord = Optional.ofNullable(newWordsModelToPersist.get(related))
                    .orElseGet(() -> {
                        //Si la entiad existe en la base de datos, se obtiene su referencia
                        WordBatchReferenceDTO ref = existingDBWordsMap.get(related);
                        if (ref != null) {
                            return entityManager.getReference(WordModel.class, ref.getId());
                        } else {
                            //Si no existe en ninguna lista, se crea un placeholder de palabra.
                            WordModel placeholder = new WordModel();
                            placeholder.setWord(related);
                            placeholder.setLength(related.length());
                            placeholder.setLanguageModel(language);
                            placeholder.setPlaceholder(true);
                            newWordsModelToPersist.put(related, placeholder);
                            return placeholder;
                        }
                    });

            relatedWordEntities.add(relatedWord);
        }

        return relatedWordEntities;
    }
}
