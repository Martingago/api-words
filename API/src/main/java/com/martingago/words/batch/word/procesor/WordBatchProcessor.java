package com.martingago.words.batch.word.procesor;

import com.martingago.words.batch.dto.DefinitionBatchDTO;
import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.DefinitionBatch;
import com.martingago.words.batch.model.ExampleBatch;
import com.martingago.words.batch.model.RelationBatch;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
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
    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap;

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap;

    //Palabras existentes en la base de datos.
    private Map<String, WordBatch> chunkWordMap = new HashMap<>();

    private ExecutionContext executionContext;


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public WordBatch process(WordBatchDTO item) throws Exception {
        // Recuperamos el mapa previamente almacenado en el ExecutionContext
        Map<String, WordBatch> wordBatchMap = (Map<String, WordBatch>) this.executionContext.get("wordBatchMap");

        // Buscamos la referencia de la palabra en el mapa
        WordBatch existingWordBatch = wordBatchMap != null ? wordBatchMap.get(item.getWord()) : null;

        WordBatch wordBatch;
        if (existingWordBatch != null) {
            // Si la palabra ya existe, comprobamos si no es un placeholder
            if (!existingWordBatch.isPlaceholder()) {
                return null;   // O ajusta la lógica según lo que necesites
            }
            // Si existe y es un placeholder, trabajamos sobre ese objeto
            wordBatch = existingWordBatch;
            wordBatch.setPlaceholder(false);
        } else {
            // Si no se encuentra en el mapa, se crea un nuevo objeto
            wordBatch = createWordBatch(item);
        }

        processDefinitions(item, wordBatch);
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
        List<DefinitionBatch> definitions = new ArrayList<>();
        if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
            List<WordBatch> placeholdersToSave = new ArrayList<>();

            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                DefinitionBatch definitionBatch = createDefinitionBatch(defDto, wordBatch);
                definitions.add(definitionBatch);

                processExamples(defDto, definitionBatch); //Procesa los ejemplos
                //processSynonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los sinónimos.
                //processAntonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los antónimos.
            }

            // Guardar todos los placeholders en un solo batch
            if (!placeholdersToSave.isEmpty()) {
                wordBatchRepository.saveAll(placeholdersToSave);
                placeholdersToSave.forEach(word -> chunkWordMap.put(word.getWord(), word));
            }
        }
        wordBatch.setDefinitions(definitions);
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
     * Gestiona las
     * @param relatedWords
     * @param placeholdersToSave
     * @param language
     * @return
     */
    private Set<WordBatch> processRelatedWords(Set<String> relatedWords, List<WordBatch> placeholdersToSave, LanguageModel language) {
        Set<WordBatch> result = new HashSet<>();
        for (String word : relatedWords) {
            WordBatch existingWord = chunkWordMap.get(word);
            if (existingWord != null) {
                result.add(existingWord);
            } else {
                WordBatch newWord = new WordBatch();
                newWord.setWord(word);
                newWord.setLength(word.length());
                newWord.setLanguage(language);
                newWord.setPlaceholder(true);
                placeholdersToSave.add(newWord);
                chunkWordMap.put(word, newWord);
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

