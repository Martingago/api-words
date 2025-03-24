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
import lombok.Getter;
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

    private Map<String, LanguageModel> languageMap;
    private Map<String, WordQualificationModel> qualificationMap;

    // Memoria local por chunk de palabras relacionadas existentes en la BBDD (Se reinicia en cada batch)
    private Map<String, WordBatch> chunkWordMap;

    // Palabras principales encontradas en la BBDD para el chunk (Determinará si la palabra se actualiza/crea/ignora)
    private Map<String, WordBatch> wordFoundedData;


    @Override
    public WordBatch process(WordBatchDTO item) throws Exception {
        // Inicializar la memoria local al comienzo del chunk
        if (chunkWordMap == null || wordFoundedData == null) {
            chunkWordMap = new HashMap<>();
            wordFoundedData = new HashMap<>();
            initializeChunkWordMap(item); // Al iniciar el batch se cargan en memoria los datos de las palabras relacionadas.
        }
        WordBatch wordBatch = createWordBatch(item); //Se persiste el objeto WordBatch en la base de datos.
        if (wordBatch == null) {
            return null;
        }
        processDefinitions(item, wordBatch);

        // Limpiar la memoria al final del chunk (esto lo manejará el writer)
        return wordBatch;
    }


    /**
     * Recorre todos los sinónimos/antónimos existentes en las definiciones de una palabra y los almacena en un map en memoria.
     * Esta funcion al aplicarse en el batch recorrerá todos los elementos word del batch y almacenará estas relaciones en memoria
     * @param dto objeto del que se quieren extraer y almacenar datos en memoria.
     */
    private void initializeChunkWordMap(WordBatchDTO dto) {
        // Recolectar todas las palabras relacionadas (sinónimos y antónimos) del chunk
        Set<String> relatedWords = new HashSet<>();
        if (dto.getDefinitions() != null) {
            for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                if (defDto.getSynonyms() != null) {
                    relatedWords.addAll(defDto.getSynonyms());
                }
                if (defDto.getAntonyms() != null) {
                    relatedWords.addAll(defDto.getAntonyms());
                }
            }
        }

        //Contiene las palabras relacionadas(sinónimos/antónimos) y la palabra principal a añadir (Para ver si es placeholder/existe en la BBDD)
        Set<String> allWordsToCheck = new HashSet<>(relatedWords);
        allWordsToCheck.add(dto.getWord());

        // Consultar todas las palabras existentes en una sola query
        Set<WordBatch> existingWords = wordBatchRepository.findByWordIn(allWordsToCheck);
        existingWords.forEach(word -> {
            if (relatedWords.contains(word.getWord())) {
                chunkWordMap.put(word.getWord(), word); // Palabras relacionadas
            }
            wordFoundedData.put(word.getWord(), word); // Todas las palabras existentes (Se usará para validar palabras existentes/placeholders)
        });

    }

    /**
     * Crea la entidad que se va a persistir en la base de datos de WordBatch
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @return objeto persistido en la BDDD.
     */
    private WordBatch createWordBatch(WordBatchDTO dto) {

        // Verificar si la palabra ya existe en wordFoundedData y en caso de existir, comprobar que sea un placeholder para actualizarlo
        WordBatch existingWord = wordFoundedData.get(dto.getWord());
        if (existingWord != null) {
            if (existingWord.isPlaceholder()) {
                // Si es placeholder, actualizarla con los nuevos datos
                existingWord.setPlaceholder(false);
                existingWord.setLength(dto.getLength());
                LanguageModel language = languageMap.get(dto.getLanguage());
                if (language == null) {
                    return null;
                }
                existingWord.setLanguage(language);
                return existingWord;
            }
            // Si existe y no es placeholder, ignorarla
            return null;
        }
        // Si la palabra no se encuentra en el map, quiere decir que no existe en la BBDD, debe ser añadida.

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
                processSynonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los sinónimos.
                processAntonyms(defDto, definitionBatch, placeholdersToSave); //Procesa los antónimos.
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

