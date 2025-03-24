package com.martingago.words.batch;

import com.martingago.words.batch.dto.DefinitionBatchDTO;
import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.DefinitionBatch;
import com.martingago.words.batch.model.ExampleBatch;
import com.martingago.words.batch.model.RelationBatch;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import com.martingago.words.batch.word.listener.WordChunkListener;
import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import com.martingago.words.batch.word.writer.FilteredWordBatchWriter;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.LanguageRepository;
import com.martingago.words.repository.WordQualificationRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final LanguageRepository languageRepository;
    private final WordBatchRepository wordBatchRepository;
    private final WordQualificationRepository wordQualificationRepository;
    private final WordBatchProcessor wordBatchProcessor;

    private Map<String, LanguageModel> languageMap; //Almacena información de los idiomas
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>(); //Almacena información de las qualifications

    @Bean
    public FlatFileItemReader<WordBatchDTO> itemReader() {
        FlatFileItemReader<WordBatchDTO> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("files/long_test.jsonl"));
        reader.setLineMapper(new JsonLineMapper<>(WordBatchDTO.class));
        return reader;
    }

    @Bean
    public JpaItemWriter<WordBatch> jpaItemWriter() {
        return new JpaItemWriterBuilder<WordBatch>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }

    @Bean
    public ItemWriter<WordBatch> filteredWordWriter() {
        return new FilteredWordBatchWriter(wordBatchRepository, jpaItemWriter());
    }

    @Bean
    public ItemProcessor<WordBatchDTO, WordBatch> wordProcessor() {
        return new ItemProcessor<WordBatchDTO, WordBatch>() {
            // Memoria local por chunk
            private Map<String, WordBatch> chunkWordMap;

            @Override
            public WordBatch process(WordBatchDTO dto) throws Exception {
                // Inicializar la memoria local al comienzo del chunk
                if (chunkWordMap == null) {
                    chunkWordMap = new HashMap<>();
                    initializeChunkWordMap(dto); // Al iniciar el batch se cargan en memoria los datos de las palabras relacionadas.
                }

                WordBatch wordBatch = createWordBatch(dto); //Se persiste el objeto WordBatch en la base de datos.
                if (wordBatch == null) {
                    return null;
                }

                processDefinitions(dto, wordBatch);

                // Limpiar la memoria al final del chunk (esto lo manejará el writer)
                return wordBatch;
            }

            /**
             * Recorre todos los sinónimos/antónimos existentes en las definiciones de una palabra y los almacena en un map en memoria.
             * Esta funcion al aplicarse en el batch recorrerá todos los elementos word del batch y almacenará estas relaciones en memoria
             * @param dto objeto del que se quieren extraer y almacenar datos en memoria.
             */
            private void initializeChunkWordMap(WordBatchDTO dto) {
                // Recolectar todos los sinónimos y antónimos del chunk
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

                // Consultar todas las palabras existentes en una sola query
                if (!relatedWords.isEmpty()) {
                    Set<WordBatch> existingWords = wordBatchRepository.findByWordIn(relatedWords);
                    existingWords.forEach(word -> chunkWordMap.put(word.getWord(), word));
                }
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
        };
    }

    @Bean
    public ItemReader<WordQualificationModel> qualificationReader() {
        List<WordQualificationModel> qualifications = wordQualificationRepository.findAll();
        return new ListItemReader<>(qualifications);
    }

    @Bean
    public ItemWriter<WordQualificationModel> qualificationWriter() {
        return items -> {
            qualificationMap = new HashMap<>();
            for (WordQualificationModel qualification : items) {
                qualificationMap.put(qualification.getQualification(), qualification);
            }
            //Se establece el qualification map en el processor
            wordBatchProcessor.setQualificationMap(qualificationMap);
        };
    }

    @Bean
    public Step getQualificationsListStep() {
        return new StepBuilder("step1", jobRepository)
                .<WordQualificationModel, WordQualificationModel>chunk(100, transactionManager)
                .reader(qualificationReader())
                .writer(qualificationWriter())


                .build();
    }

    @Bean
    public Step getLanguagesListStep() {
        return new StepBuilder("step0", jobRepository)
                .<LanguageModel, LanguageModel>chunk(100, transactionManager)
                .reader(languageReader())
                .writer(languageWriter())
                .build();
    }

    @Bean
    public WordChunkListener wordChunkListener() {
        return new WordChunkListener();
    }

    @Bean
    public ItemReader<LanguageModel> languageReader() {
        List<LanguageModel> languages = languageRepository.findAll();
        return new ListItemReader<>(languages);
    }

    @Bean
    public ItemWriter<LanguageModel> languageWriter() {
        return items -> {
            languageMap = new HashMap<>();
            for (LanguageModel language : items) {
                languageMap.put(language.getLangCode(), language);
            }
            //Se le pasa al wordBatchProcessor el map de idiomas
            wordBatchProcessor.setLanguageMap(languageMap);
        };
    }

    @Bean
    public Step addWordStep() {
        return new StepBuilder("wordBatchStep", jobRepository)
                .<WordBatchDTO, WordBatch>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(wordBatchProcessor)
                .writer(filteredWordWriter())
                .listener(wordChunkListener())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("wordJob", jobRepository)
                .start(getLanguagesListStep())
                .next(getQualificationsListStep())
                .next(addWordStep())
                .build();
    }
}