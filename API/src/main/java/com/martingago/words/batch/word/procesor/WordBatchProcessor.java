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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordBatch>, ItemStream {

    private final WordQualificationRepository wordQualificationRepository;
    private final WordMapper wordMapper;
    private final WordBatchRepository wordBatchRepository;
    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap = new HashMap<>();;

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>();

    //Palabras ya existentes en la base de datos.
    private Map<String, WordBatchReferenceDTO> chunkWordMap = new HashMap<>();

    //Palabras que se van a persistir en la base de datos.
    private Map<String, WordBatch> newWordBatchMap = new HashMap<>();

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
    public WordBatch process(WordBatchDTO item) throws Exception {
        // Recuperamos el mapa previamente almacenado en el ExecutionContext
        chunkWordMap = (Map<String, WordBatchReferenceDTO>) this.executionContext.get("wordBatchMap");
        newWordBatchMap = (Map<String, WordBatch>) this.executionContext.get("newWordsToPersistMap");
        WordBatch wordBatch;

        // PRIMERA COMPROBACIÓN: Verificar si la palabra ya está en el mapa temporal de palabras nuevas
        wordBatch = newWordBatchMap.get(item.getWord());
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
                return null;   // Ya existe como palabra completa, no placeholder
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
     * @param dto
     * @param wordBatch
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

    /**
     *
     * @param defDto DTO que contiene la información del objeto a procesar
     * @param definitionBatch definición sobre la que se van a crear las relaciones
     */
    private void processSynonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch) {
        if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
            Set<WordBatch> synonymWords = processRelatedWords(defDto.getSynonyms(), definitionBatch.getWord().getLanguage());
            Set<RelationBatch> synonymRelations = createWordRelations(definitionBatch, synonymWords, RelationEnumType.SINONIMA);
            definitionBatch.setSynonymRelations(synonymRelations);
        }
    }

    /**
     *
     * @param defDto
     * @param definitionBatch
     */
    private void processAntonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch) {
        if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
            Set<WordBatch> antonymWords = processRelatedWords(defDto.getAntonyms(), definitionBatch.getWord().getLanguage());
            Set<RelationBatch> antonymRelations = createWordRelations(definitionBatch, antonymWords, RelationEnumType.ANTONIMA);
            definitionBatch.setAntonymRelations(antonymRelations);
        }
    }

    /**
     * Gestiona las palabras relacionadas (sinónimos/antónimos)
     * @param relatedWords Conjunto de palabras relacionadas
     * @param language Idioma de las palabras
     * @return Conjunto de entidades WordBatch (reales o referencias)
     */
    private Set<WordBatch> processRelatedWords(Set<String> relatedWords, LanguageModel language) {
        Set<WordBatch> result = new HashSet<>();

        for (String word : relatedWords) {
            // Primero buscamos en el mapa de palabras nuevas de este lote
            WordBatch newWord = newWordBatchMap.get(word);

            if (newWord != null) {
                // La palabra ya está creada (pero no persistida) en este lote
                result.add(newWord);
                continue;
            }

            // Si no está en las nuevas, buscamos en las existentes
            WordBatchReferenceDTO existingWordRef = chunkWordMap != null ? chunkWordMap.get(word) : null;
            if (existingWordRef != null) {
                // Ya existe en la BD
                WordBatch wordBatch = entityManager.getReference(WordBatch.class, existingWordRef.getId());
                result.add(wordBatch);
            } else {
                // No existe ni en el lote ni en la BD, crear placeholder
                newWord = new WordBatch();
                newWord.setWord(word);
                newWord.setLength(word.length());
                newWord.setLanguage(language);
                newWord.setPlaceholder(true);

                // Importante: almacenar en el mapa temporal sin persistir todavía
                newWordBatchMap.put(word, newWord);

                result.add(newWord);
            }
        }
        return result;
    }

    private Set<RelationBatch> createWordRelations(DefinitionBatch definitionBatch, Set<WordBatch> relatedWords, RelationEnumType relationType) {
        Set<RelationBatch> relations = new HashSet<>();
        for (WordBatch relatedWord : relatedWords) {
            RelationBatch relation = new RelationBatch();
            relation.setDefinitionBatch(definitionBatch);
            relation.setWordRelated(relatedWord);
            relation.setRelationEnumType(relationType);
            relations.add(relation);
        }
        return relations;
    }

}

