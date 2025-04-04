package com.martingago.words.batch.word.procesor;

import com.martingago.words.batch.dto.DefinitionBatchDTO;
import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.dto.WordBatchReferenceDTO;
import com.martingago.words.batch.model.DefinitionBatch;
import com.martingago.words.batch.model.ExampleBatch;
import com.martingago.words.batch.model.RelationBatch;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordBatch>, ItemStream, StepExecutionListener {

    private final WordQualificationRepository wordQualificationRepository;

    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap;

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>();

    //Palabras ya existentes en la base de datos
    private Map<String, WordBatchReferenceDTO> chunkWordMap = new HashMap<>();

    //Palabras que se van a persistir en la base de datos
    private Map<String, WordBatch> newWordBatchMap = new HashMap<>();

    private StepExecution stepExecution;

    @PersistenceContext
    private EntityManager entityManager;

    private ExecutionContext jobContext;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution; // Captura el StepExecution
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // Inicializa los mapas desde el Job ExecutionContext
        this.jobContext = stepExecution.getJobExecution().getExecutionContext();

        this.languageMap = (Map<String, LanguageModel>) jobContext.get("languageMap");
        System.out.println("tamaño del language map: " + languageMap.size());
    }


    @Override
    public WordBatch process(WordBatchDTO item) throws Exception {
        this.chunkWordMap = (Map<String, WordBatchReferenceDTO>) jobContext.get("wordBatchMap");
        this.newWordBatchMap = (Map<String, WordBatch>) jobContext.get("newWordsToPersistMap");
        // PRIMERA COMPROBACIÓN: Verificar si la palabra ya está en el mapa temporal de palabras nuevas
        WordBatch wordBatch = newWordBatchMap.get(item.getWord());
        if (wordBatch != null) {
            // La palabra ya se ha creado en este lote como placeholder
            // Actualizamos para que ya no sea placeholder y añadimos datos completos
            wordBatch.setPlaceholder(false);
            processDefinitions(item, wordBatch);
            return wordBatch;
        }

        // SEGUNDA COMPROBACIÓN: Verificar si existe en la base de datos
        WordBatchReferenceDTO existingWordBatch = chunkWordMap != null ? chunkWordMap.get(item.getWord()) : null;
        if (existingWordBatch != null) {
            // Si la palabra ya existe, comprobamos si no es un placeholder
            if (!existingWordBatch.isPlaceholder()) {
                return null; // Ya existe como palabra completa, no placeholder
            }
            // Si existe y es un placeholder, trabajamos sobre ese objeto
            wordBatch = entityManager.getReference(WordBatch.class, existingWordBatch.getId());
            wordBatch.setPlaceholder(false);
        } else {
            // Si no se encuentra en ninguno de los mapas, se crea un nuevo objeto
            wordBatch = createWordBatch(item);
            if (wordBatch != null) {
                // Almacenar en el mapa temporal de palabras nuevas (no persistidas)
                newWordBatchMap.put(item.getWord(), wordBatch);
            }
        }

        if (wordBatch != null) {
            processDefinitions(item, wordBatch);
        }

        return wordBatch;
    }

    /**
     * Crea la entidad que se va a persistir en la base de datos de WordBatch
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @return objeto persistido en la BDDD.
     */
    private WordBatch createWordBatch(WordBatchDTO dto) {
        WordBatch wordBatch = new WordBatch();
        wordBatch.setWord(dto.getWord());
        wordBatch.setLength(dto.getLength());
        wordBatch.setPlaceholder(false);

        LanguageModel language = languageMap.get(dto.getLanguage());
        if (language == null) {
            return null;
        }
        wordBatch.setLanguage(language);
        return wordBatch;
    }

    /**
     * Procesa las creaciones de definiciones y coordina las relaciones con las otras palabras
     * @param dto DTO de entrada
     * @param wordBatch entidad principal sobre la que se procesarán las definiciones
     */
    private void processDefinitions(WordBatchDTO dto, WordBatch wordBatch) {
        wordBatch.getDefinitions().clear();

        if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                DefinitionBatch definitionBatch = createDefinitionBatch(defDto, wordBatch);
                wordBatch.getDefinitions().add(definitionBatch);

                processExamples(defDto, definitionBatch);
                processSynonyms(defDto, definitionBatch);
                processAntonyms(defDto, definitionBatch);
            }
        }
    }

    private DefinitionBatch createDefinitionBatch(DefinitionBatchDTO defDto, WordBatch wordBatch) {
        DefinitionBatch definitionBatch = new DefinitionBatch();
        definitionBatch.setDefinition(defDto.getDefinition());

        WordQualificationModel qualificationModel = qualificationMap.get(defDto.getQualification());
        if (qualificationModel == null) {
            qualificationModel = new WordQualificationModel();
            qualificationModel.setQualification(defDto.getQualification());
            qualificationModel = wordQualificationRepository.save(qualificationModel);
            qualificationMap.put(qualificationModel.getQualification(), qualificationModel);
        }
        definitionBatch.setWordQualificationModel(qualificationModel);
        definitionBatch.setWord(wordBatch);

        return definitionBatch;
    }

    private void processExamples(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch) {
        Set<ExampleBatch> examples = new HashSet<>();
        if (defDto.getExamples() != null && !defDto.getExamples().isEmpty()) {
            for (String ex : defDto.getExamples()) {
                ExampleBatch example = new ExampleBatch();
                example.setExample(ex);
                example.setDefinitionBatch(definitionBatch);
                examples.add(example);
            }
        }
        definitionBatch.setExamples(examples);
    }

    private void processSynonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch) {
        if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
            Set<WordBatch> synonymWords = processRelatedWords(defDto.getSynonyms(), definitionBatch.getWord().getLanguage());
            Set<RelationBatch> synonymRelations = createWordRelations(definitionBatch, synonymWords, RelationEnumType.SINONIMA);
            definitionBatch.setSynonymRelations(synonymRelations);
        }
    }

    private void processAntonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch) {
        if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
            Set<WordBatch> antonymWords = processRelatedWords(defDto.getAntonyms(), definitionBatch.getWord().getLanguage());
            Set<RelationBatch> antonymRelations = createWordRelations(definitionBatch, antonymWords, RelationEnumType.ANTONIMA);
            definitionBatch.setAntonymRelations(antonymRelations);
        }
    }

    private Set<WordBatch> processRelatedWords(Set<String> relatedWords, LanguageModel language) {
        Set<WordBatch> relatedWordEntities = new HashSet<>();

        for (String related : relatedWords) {
            WordBatch relatedWord = Optional.ofNullable(newWordBatchMap.get(related))
                    .orElseGet(() -> {
                        WordBatchReferenceDTO ref = chunkWordMap.get(related);
                        if (ref != null) {
                            return entityManager.getReference(WordBatch.class, ref.getId());
                        } else {
                            WordBatch placeholder = new WordBatch();
                            placeholder.setWord(related);
                            placeholder.setLength(related.length());
                            placeholder.setLanguage(language);
                            placeholder.setPlaceholder(true);
                            newWordBatchMap.put(related, placeholder);
                            return placeholder;
                        }
                    });

            relatedWordEntities.add(relatedWord);
        }

        return relatedWordEntities;
    }

    private Set<RelationBatch> createWordRelations(DefinitionBatch definition, Set<WordBatch> relatedWords, RelationEnumType type) {
        Set<RelationBatch> relations = new HashSet<>();
        for (WordBatch relatedWord : relatedWords) {
            RelationBatch relation = new RelationBatch();
            relation.setDefinitionBatch(definition);
            relation.setWordRelated(relatedWord);
            relation.setRelationEnumType(type);
            relations.add(relation);
        }
        return relations;
    }
}
