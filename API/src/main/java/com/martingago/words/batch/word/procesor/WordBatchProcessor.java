package com.martingago.words.batch.word.procesor;

import com.martingago.words.context.DefinitionProcessedContext;
import com.martingago.words.domain.model.*;
import com.martingago.words.domain.service.definition.CreateDefinitionService;
import com.martingago.words.domain.service.example.CreateExampleService;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.domain.service.relation.CreateWordRelationsService;
import com.martingago.words.dto.word.request.WordBatchDTO;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.domain.repository.WordQualificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordModel>, ItemStream {

    private final CreateWordModelService createWordModelService;
    private final CreateDefinitionService createDefinitionService;

    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap = new HashMap<>();

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>();

    //Palabras ya existentes en la base de datos
    private Map<String, WordBatchReferenceDTO> chunkWordMap = new HashMap<>();

    //Palabras que se van a persistir en la base de datos
    private Map<String, WordModel> newWordBatchMap = new HashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        newWordBatchMap.clear();
    }

    @Override
    public WordModel process(WordBatchDTO item) throws Exception {
        // Recuperamos el mapa previamente almacenado en el ExecutionContext
        chunkWordMap = (Map<String, WordBatchReferenceDTO>) this.executionContext.get("wordBatchMap");
        newWordBatchMap = (Map<String, WordModel>) this.executionContext.get("newWordsToPersistMap");

        WordModel wordModel;

        // PRIMERA COMPROBACIÓN: Verificar si la palabra ya está en el mapa temporal de palabras nuevas
        wordModel = newWordBatchMap.get(item.getWord());
        if (wordModel != null) {
            // La palabra ya se ha creado en este lote como placeholder
            // Actualizamos para que ya no sea placeholder y añadimos datos completos
            wordModel.setPlaceholder(false);
            createDefinitionService.processDefinitions(item, wordModel, qualificationMap, newWordBatchMap, chunkWordMap);
            return wordModel;
        }

        // SEGUNDA COMPROBACIÓN: Verificar si existe en la base de datos
        WordBatchReferenceDTO existingWordBatch = chunkWordMap != null ? chunkWordMap.get(item.getWord()) : null;
        if (existingWordBatch != null) {
            // Si la palabra ya existe, comprobamos si no es un placeholder
            if (!existingWordBatch.isPlaceholder()) {
                return null; // Ya existe como palabra completa, no placeholder
            }
            // Si existe y es un placeholder, trabajamos sobre ese objeto
            wordModel = entityManager.getReference(WordModel.class, existingWordBatch.getId());
            wordModel.setPlaceholder(false);
        } else {
            // Si no se encuentra en ninguno de los mapas, se crea un nuevo objeto
            LanguageModel language = languageMap.get(item.getLanguage());
            if(language != null){
                wordModel = createWordModelService.createWord(item,language);
            }
            if (wordModel != null) {
                // Almacenar en el mapa temporal de palabras nuevas (no persistidas)
                newWordBatchMap.put(item.getWord(), wordModel);
            }
        }

        if (wordModel != null) {
            createDefinitionService.processDefinitions(item, wordModel, qualificationMap, newWordBatchMap, chunkWordMap);
        }
        return wordModel;
    }

}
