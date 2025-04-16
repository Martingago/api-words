package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.*;
import com.martingago.words.domain.service.definition.CreateDefinitionService;
import com.martingago.words.dto.models.word.request.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.response.WordDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Map;


@RequiredArgsConstructor
@Service
public class CreateWordModelService {

    private final CreateDefinitionService createDefinitionService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Añade una palabra con sus definiciones a la base de datos
     *
     * @param wordDTO    objeto con la información que se quiere persistir.
     * @param languageMap            map con los idiomas existentes en la base de datos.
     * @param qualificationMap       map con la información de las qualifications existentes en la base de datos.
     * @param newWordsModelToPersist map con la información de las palabras que se van a persistir en la base de datos.
     * @param existingDBWordsMap     map con la información de las palabras que ya existen en la base de datos.
     * @return WordModel que se va a persistir en la base de datos.
     */
    public WordModel processWordDTOintoWordModel(WordDTO wordDTO,
                                                 Map<String, LanguageModel> languageMap,
                                                 Map<String, WordQualificationModel> qualificationMap,
                                                 Map<String, WordModel> newWordsModelToPersist,
                                                 Map<String, SimpleWordSerializableDTO> existingDBWordsMap
                                            ){
        WordModel wordModel;

        // PRIMERA COMPROBACIÓN: Verificar si la palabra ya está en el mapa temporal de palabras nuevas
        wordModel = newWordsModelToPersist.get(wordDTO.getWord());
        if (wordModel != null) {
            // La palabra ya se ha creado en este lote como placeholder
            // Actualizamos para que ya no sea placeholder y añadimos datos completos
            wordModel.setPlaceholder(false);
            createDefinitionService.processDefinitions(wordDTO, wordModel, qualificationMap, newWordsModelToPersist, existingDBWordsMap);
            return wordModel;
        }

        // SEGUNDA COMPROBACIÓN: Verificar si existe en la base de datos
        SimpleWordSerializableDTO existingWordBatch = existingDBWordsMap != null ? existingDBWordsMap.get(wordDTO.getWord()) : null;
        if (existingWordBatch != null) {
            // Si la palabra ya existe, comprobamos si no es un placeholder
            if (!existingWordBatch.isPlaceholder()) {
                //En caso de que la palabra ya exista y no sea un placeholder se envía como una entidad duplicada.
                throw new DuplicateKeyException("Word: " + wordDTO.getWord() + " already exists on Database");
            }
            // Si existe y es un placeholder, trabajamos sobre ese objeto
            wordModel = entityManager.getReference(WordModel.class, existingWordBatch.getId());
            wordModel.setPlaceholder(false);
        } else {
            // Si no se encuentra en ninguno de los mapas, se crea un nuevo objeto
            LanguageModel language = languageMap.get(wordDTO.getLanguage());
            if(language != null){
                wordModel = createWord(wordDTO,language);
            }
            if (wordModel != null) {
                // Almacenar en el mapa temporal de palabras nuevas (no persistidas)
                newWordsModelToPersist.put(wordDTO.getWord(), wordModel);
            }
        }

        if (wordModel != null) {
            createDefinitionService.processDefinitions(wordDTO, wordModel, qualificationMap, newWordsModelToPersist, existingDBWordsMap);
        }
        return wordModel;
    }

    /**
     /**
     * Crea la entidad que se va a persistir en la base de datos de WordModel
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @param languageModel con el que se debe asociar la palabra a crear en la base de datos.
     * @return objeto WordModel persistido en la BDDD.
     */
    public WordModel createWord(WordDTO dto, LanguageModel languageModel) {
        WordModel wordModel = new WordModel();
        wordModel.setWord(dto.getWord());
        wordModel.setLength(dto.getLength());
        wordModel.setPlaceholder(false);

        if (languageModel == null) {
            return null;
        }
        wordModel.setLanguageModel(languageModel);
        return wordModel;
    }

}
