package com.martingago.words.batch.word.procesor;

import com.martingago.words.batch.dto.DefinitionBatchDTO;
import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.dto.WordBatchReferenceDTO;
import com.martingago.words.model.*;
import com.martingago.words.repository.WordQualificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordModel>, ItemStream {

    private final WordQualificationRepository wordQualificationRepository;

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
            processDefinitions(item, wordModel);
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
            wordModel = createWordBatch(item);
            if (wordModel != null) {
                // Almacenar en el mapa temporal de palabras nuevas (no persistidas)
                newWordBatchMap.put(item.getWord(), wordModel);
            }
        }

        if (wordModel != null) {
            processDefinitions(item, wordModel);
        }

        return wordModel;
    }

    /**
     * Crea la entidad que se va a persistir en la base de datos de WordModel
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @return objeto persistido en la BDDD.
     */
    private WordModel createWordBatch(WordBatchDTO dto) {
        WordModel wordModel = new WordModel();
        wordModel.setWord(dto.getWord());
        wordModel.setLength(dto.getLength());
        wordModel.setPlaceholder(false);

        LanguageModel language = languageMap.get(dto.getLanguage());
        if (language == null) {
            return null;
        }
        wordModel.setLanguageModel(language);
        return wordModel;
    }

    /**
     * Procesa las creaciones de definiciones y coordina las relaciones con las otras palabras
     * @param dto DTO de entrada
     * @param wordModel entidad principal sobre la que se procesarán las definiciones
     */
    private void processDefinitions(WordBatchDTO dto, WordModel wordModel) {
        wordModel.getWordDefinitionModelSet().clear();

        if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                WordDefinitionModel definitionBatch = createDefinitionBatch(defDto, wordModel);
                wordModel.getWordDefinitionModelSet().add(definitionBatch);

                processExamples(defDto, definitionBatch);
                processSynonyms(defDto, definitionBatch);
                processAntonyms(defDto, definitionBatch);
            }
        }
    }

    private WordDefinitionModel createDefinitionBatch(DefinitionBatchDTO defDto, WordModel wordModel) {
        WordDefinitionModel wordDefinitionModel = new WordDefinitionModel();
        wordDefinitionModel.setWordDefinition(defDto.getDefinition());

        WordQualificationModel qualificationModel = qualificationMap.get(defDto.getQualification());
        if (qualificationModel == null) {
            qualificationModel = new WordQualificationModel();
            qualificationModel.setQualification(defDto.getQualification());
            qualificationModel = wordQualificationRepository.save(qualificationModel);
            qualificationMap.put(qualificationModel.getQualification(), qualificationModel);
        }
        //Establece la qualification
        wordDefinitionModel.setWordQualificationModel(qualificationModel);

        //Establece la palabra
        wordDefinitionModel.setWord(wordModel);

        return wordDefinitionModel;
    }

    /**
     *
     * @param defDto
     * @param wordDefinitionModel
     */
    private void processExamples(DefinitionBatchDTO defDto, WordDefinitionModel wordDefinitionModel) {
        Set<WordExampleModel> examples = new HashSet<>();
        if (defDto.getExamples() != null && !defDto.getExamples().isEmpty()) {
            for (String ex : defDto.getExamples()) {
                WordExampleModel example = new WordExampleModel();
                example.setExample(ex);
                example.setWordDefinitionModel(wordDefinitionModel);
                examples.add(example);
            }
        }
        wordDefinitionModel.setWordExampleModelSet(examples);
    }

    private void processSynonyms(DefinitionBatchDTO defDto, WordDefinitionModel wordDefinitionModel) {
        if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
            Set<WordModel> synonymWords = processRelatedWords(defDto.getSynonyms(), wordDefinitionModel.getWord().getLanguageModel());
            Set<WordRelationModel> synonymRelations = createWordRelations(wordDefinitionModel, synonymWords, RelationEnumType.SINONIMA);
            wordDefinitionModel.setSynonymRelationsSet(synonymRelations);
        }
    }

    private void processAntonyms(DefinitionBatchDTO defDto, WordDefinitionModel definitionBatch) {
        if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
            Set<WordModel> antonymWords = processRelatedWords(defDto.getAntonyms(), definitionBatch.getWord().getLanguageModel());
            Set<WordRelationModel> antonymRelations = createWordRelations(definitionBatch, antonymWords, RelationEnumType.ANTONIMA);
            definitionBatch.setAntonymRelationsSet(antonymRelations);
        }
    }

    private Set<WordModel> processRelatedWords(Set<String> relatedWords, LanguageModel language) {
        Set<WordModel> relatedWordEntities = new HashSet<>();

        for (String related : relatedWords) {
            WordModel relatedWord = Optional.ofNullable(newWordBatchMap.get(related))
                    .orElseGet(() -> {
                        WordBatchReferenceDTO ref = chunkWordMap.get(related);
                        if (ref != null) {
                            return entityManager.getReference(WordModel.class, ref.getId());
                        } else {
                            WordModel placeholder = new WordModel();
                            placeholder.setWord(related);
                            placeholder.setLength(related.length());
                            placeholder.setLanguageModel(language);
                            placeholder.setPlaceholder(true);
                            newWordBatchMap.put(related, placeholder);
                            return placeholder;
                        }
                    });

            relatedWordEntities.add(relatedWord);
        }

        return relatedWordEntities;
    }

    private Set<WordRelationModel> createWordRelations(WordDefinitionModel definition, Set<WordModel> relatedWords, RelationEnumType type) {
        Set<WordRelationModel> relations = new HashSet<>();
        for (WordModel relatedWord : relatedWords) {
            WordRelationModel relation = new WordRelationModel();
            relation.setWordDefinitionModel(definition);
            relation.setWordRelated(relatedWord);
            relation.setRelationEnumType(type);
            relations.add(relation);
        }
        return relations;
    }
}
