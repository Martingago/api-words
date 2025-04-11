package com.martingago.words.batch.word.procesor;

import com.martingago.words.domain.model.*;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.dto.word.request.WordBatchDTO;
import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordModel>, ItemStream {

    private final CreateWordModelService createWordModelService;


    private Map<String, LanguageModel> languageMap = new HashMap<>(); //Memoria local idiomas existentes.
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>(); //Memoria local qualifications existentes
    private Map<String, WordModel> newWordsModelToPersist = new HashMap<>(); //Memoria local palabras relacionadas a persistir
    private Map<String, WordBatchReferenceDTO> existingDBWordsMap = new HashMap<>(); //Memoria local palabras existentes

    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        newWordsModelToPersist.clear();
    }

    @Override
    public WordModel process(WordBatchDTO item) throws Exception {
        // Recuperamos el mapa previamente almacenado en el ExecutionContext
        existingDBWordsMap = (Map<String, WordBatchReferenceDTO>) this.executionContext.get("wordBatchMap");
        newWordsModelToPersist = (Map<String, WordModel>) this.executionContext.get("newWordsToPersistMap");

        try{
            //Se llama a la función encargada de procesar los datos de una palabra en las diferentes entidades que la componen.
            return createWordModelService.insertWordIntoDatabase(item,
                    languageMap,
                    qualificationMap,
                    newWordsModelToPersist,
                    existingDBWordsMap);
        }catch (DuplicateKeyException e){
            return null;
        }
    }
}
