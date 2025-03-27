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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Component
@RequiredArgsConstructor
public class WordBatchProcessor implements ItemProcessor<WordBatchDTO, WordBatch> {

    private final WordBatchRepository wordBatchRepository;
    private final WordQualificationRepository wordQualificationRepository;
    //Memoria local para almacenar los idiomas existentes
    private Map<String, LanguageModel> languageMap;

    //Memoria local para almacenar las qualificaciones existentes
    private Map<String, WordQualificationModel> qualificationMap;



    @Override
    public WordBatch process(WordBatchDTO item) throws Exception {
        //Se comprueba en la BBDD la existencia de la palabra.
        WordBatch wordBatch = null;
        WordBatch existingWordBatch = wordBatchRepository.findByWord(item.getWord());

        if(existingWordBatch != null){
            //Si no es un placeholder se salta
            if(!existingWordBatch.isPlaceholder()){
                return null;
            }
            // Si existe en la BBDD y no es un placeholder se actualiza su información.
            wordBatch = existingWordBatch;
            wordBatch.setPlaceholder(false);

        }else{
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
            //List<WordBatch> placeholdersToSave = new ArrayList<>();

            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                DefinitionBatch definitionBatch = createDefinitionBatch(defDto, wordBatch);
                definitions.add(definitionBatch);

                processExamples(defDto, definitionBatch); //Procesa los ejemplos
                //processSynonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los sinónimos.
                //processAntonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los antónimos.
            }

            // Guardar todos los placeholders en un solo batch
//            if (!placeholdersToSave.isEmpty()) {
//                wordBatchRepository.saveAll(placeholdersToSave);
//                placeholdersToSave.forEach(word -> chunkWordMap.put(word.getWord(), word));
//            }
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
//        for (String word : relatedWords) {
//            WordBatch existingWord = chunkWordMap.get(word);
//            if (existingWord != null) {
//                result.add(existingWord);
//            } else {
//                WordBatch newWord = new WordBatch();
//                newWord.setWord(word);
//                newWord.setLength(word.length());
//                newWord.setLanguage(language);
//                newWord.setPlaceholder(true);
//                placeholdersToSave.add(newWord);
//                chunkWordMap.put(word, newWord);
//                result.add(newWord);
//            }
//        }
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

