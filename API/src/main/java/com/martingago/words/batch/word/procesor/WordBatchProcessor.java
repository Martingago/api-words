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

    private final WordBatchRepository wordBatchRepository;
    private final WordQualificationRepository wordQualificationRepository;
    private final WordMapper wordMapper;

    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap = new HashMap<>();;

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>();

    //Palabras existentes en la base de datos.
    private Map<String, WordBatchReferenceDTO> chunkWordMap = new HashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    private ExecutionContext executionContext;


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public WordBatch process(WordBatchDTO item) throws Exception {
        // Recuperamos el mapa previamente almacenado en el ExecutionContext
        chunkWordMap = (Map<String, WordBatchReferenceDTO>) this.executionContext.get("wordBatchMap");

        // Buscamos la referencia de la palabra en el mapa
        WordBatchReferenceDTO existingWordBatch = chunkWordMap != null ? chunkWordMap.get(item.getWord()) : null;

        WordBatch wordBatch;
        if (existingWordBatch != null) {
            // Si la palabra ya existe, comprobamos si no es un placeholder
            if (!existingWordBatch.isPlaceholder()) {
                return null;   // O ajusta la lógica según lo que necesites
            }
            // Si existe y es un placeholder, trabajamos sobre ese objeto
            wordBatch = entityManager.getReference(WordBatch.class, existingWordBatch.getId());
            wordBatch.setPlaceholder(false);
        } else {
            // Si no se encuentra en el mapa, se crea un nuevo objeto
            wordBatch = createWordBatch(item);
        }

        processDefinitions(item, wordBatch);
        // Actualizar el mapa en el ExecutionContext después de procesar
        this.executionContext.put("wordBatchMap", chunkWordMap);
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
        // Limpia las definiciones existentes
        wordBatch.getDefinitions().clear();

        if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
            List<WordBatch> placeholdersToSave = new ArrayList<>();

            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                DefinitionBatch definitionBatch = createDefinitionBatch(defDto, wordBatch);
                wordBatch.getDefinitions().add(definitionBatch); // Añade a la colección existente

                processExamples(defDto, definitionBatch);
                processSynonyms(defDto, definitionBatch, placeholdersToSave);
                processAntonyms(defDto, definitionBatch, placeholdersToSave);
            }

            if (!placeholdersToSave.isEmpty()) {
                wordBatchRepository.saveAll(placeholdersToSave);
                placeholdersToSave.forEach(word -> chunkWordMap.put(word.getWord(), wordMapper.toWordReference(word)));
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
     * @param defDto
     * @param definitionBatch
     * @param placeholdersToSave
     */
    private void processSynonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch, List<WordBatch> placeholdersToSave) {
        if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
            Set<WordBatch> synonymWords = processRelatedWords(defDto.getSynonyms(), placeholdersToSave, definitionBatch.getWord().getLanguage());
            Set<RelationBatch> synonymRelations = createWordRelations(definitionBatch, synonymWords, RelationEnumType.SINONIMA);
            definitionBatch.setSynonymRelations(synonymRelations);
        }
    }

    /**
     *
     * @param defDto
     * @param definitionBatch
     * @param placeholdersToSave
     */
    private void processAntonyms(DefinitionBatchDTO defDto, DefinitionBatch definitionBatch, List<WordBatch> placeholdersToSave) {
        if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
            Set<WordBatch> antonymWords = processRelatedWords(defDto.getAntonyms(), placeholdersToSave, definitionBatch.getWord().getLanguage());
            Set<RelationBatch> antonymRelations = createWordRelations(definitionBatch, antonymWords, RelationEnumType.ANTONIMA);
            definitionBatch.setAntonymRelations(antonymRelations);
        }
    }

    /**
     * Gestiona las palabras relacionadas (sinónimos/antónimos)
     * @param relatedWords Conjunto de palabras relacionadas
     * @param placeholdersToSave Lista donde se añaden nuevos placeholders para guardar
     * @param language Idioma de las palabras
     * @return Conjunto de entidades WordBatch (reales o referencias)
     */
    private Set<WordBatch> processRelatedWords(Set<String> relatedWords, List<WordBatch> placeholdersToSave, LanguageModel language) {
        Set<WordBatch> result = new HashSet<>();

        for (String word : relatedWords) {
            WordBatchReferenceDTO existingWordRef = chunkWordMap != null ? chunkWordMap.get(word) : null;

            if (existingWordRef != null) {
                // Crear una referencia de WordBatch con el ID existente
                WordBatch wordBatch = entityManager.getReference(WordBatch.class, existingWordRef.getId());
                // Solo añadimos los campos mínimos necesarios para mantener la relación
                result.add(wordBatch);
            } else {
                // Crear un nuevo placeholder
                WordBatch newWord = new WordBatch();
                newWord.setWord(word);
                newWord.setLength(word.length());

                // Establecer referencia al idioma
                newWord.setLanguage(language);
                newWord.setPlaceholder(true);

                // Añadir a la lista para guardar
                placeholdersToSave.add(newWord);

                // Después de guardar, asegurarse de actualizar el mapa de referencias
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

