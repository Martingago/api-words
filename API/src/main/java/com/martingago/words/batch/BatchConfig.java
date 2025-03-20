package com.martingago.words.batch;

import com.martingago.words.batch.dto.DefinitionBatchDTO;
import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.DefinitionBatch;
import com.martingago.words.batch.model.ExampleBatch;
import com.martingago.words.batch.model.RelationBatch;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
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
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final LanguageRepository languageRepository;
    private final WordBatchRepository wordBatchRepository;
    private final WordQualificationRepository wordQualificationRepository;

    private Map<String, LanguageModel> languageMap;
    private Map<String, WordQualificationModel> qualificationMap = new HashMap<>();
    private Map<String, WordBatch> existingWordsMap = new HashMap<>();

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
                .build();
    }

    @Bean
    public ItemWriter<WordBatch> filteredWordWriter() {
        return new FilteredWordBatchWriter(wordBatchRepository, jpaItemWriter());
    }

    /**
     * Procesa la creación de una palabra. Para poder procesar correctamente la palabra es necesario comprobar desde memoria que el idioma
     * relacionado exista con anterioridad. Esto se consigue en el step anterior.
     */
    @Bean
    public ItemProcessor<WordBatchDTO, WordBatch> wordProcessor() {
        return dto -> {
            WordBatch wordBatch = new WordBatch();
            wordBatch.setWord(dto.getWord());
            wordBatch.setLength(dto.getLength());
            wordBatch.setPlaceholder(false); // Por defecto las palabras que se añaden no son placeholders

            // Buscar el LanguageModel en el Map
            LanguageModel language = languageMap.get(dto.getLanguage());
            if (language == null) {
                return null; // Si el idioma no existe se salta la palabra.
            }
            wordBatch.setLanguage(language);

            // Procesar las definiciones
            List<DefinitionBatch> definitions = new ArrayList<>();
            if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
                for (DefinitionBatchDTO defDto : dto.getDefinitions()) {
                    DefinitionBatch definitionBatch = new DefinitionBatch();
                    definitionBatch.setDefinition(defDto.getDefinition());

                    // Se añade la qualification a la definición de la palabra:
                    WordQualificationModel qualificationModel = qualificationMap.get(defDto.getQualification());
                    if (qualificationModel == null) {
                        qualificationModel = new WordQualificationModel();
                        qualificationModel.setQualification(defDto.getQualification());
                        qualificationModel = wordQualificationRepository.save(qualificationModel);
                        qualificationMap.put(qualificationModel.getQualification(), qualificationModel);
                    }
                    definitionBatch.setWordQualificationModel(qualificationModel);
                    definitionBatch.setWord(wordBatch);

                    // Se establecen los ejemplos existentes para cada definición.
                    Set<ExampleBatch> examples = new HashSet<>();
                    if (defDto.getExamples() != null && !defDto.getExamples().isEmpty()) {
                        for (String ex : defDto.getExamples()) {
                            ExampleBatch example = new ExampleBatch();
                            example.setExample(ex);
                            example.setDefinitionBatch(definitionBatch);
                            examples.add(example);
                        }
                    }
                    definitionBatch.setExamples(examples); // Añade los examples dentro del objeto de las definitions

                    // Procesar sinónimos
                    if (defDto.getSynonyms() != null && !defDto.getSynonyms().isEmpty()) {
                        // Obtener palabras existentes de la base de datos
                        List<String> synonymWords = defDto.getSynonyms();
                        List<WordBatch> existingSynonyms = findExistingWords(synonymWords);

                        // Crear placeholders para palabras que no existen
                        List<WordBatch> newSynonyms = createPlaceholderWords(synonymWords, existingSynonyms, language);

                        // Combinar todas las palabras sinónimas
                        List<WordBatch> allSynonyms = new ArrayList<>();
                        allSynonyms.addAll(existingSynonyms);
                        allSynonyms.addAll(newSynonyms);

                        // Crear relaciones de sinónimos
                        Set<RelationBatch> synonymRelations = createWordRelations(definitionBatch, allSynonyms, RelationEnumType.SINONIMA);

                        definitionBatch.setSynonymRelations(synonymRelations);
                    }

                    // Procesar antónimos (similar a sinónimos pero con tipo ANTONIMA)
                    if (defDto.getAntonyms() != null && !defDto.getAntonyms().isEmpty()) {
                        List<String> antonymWords = defDto.getAntonyms();
                        List<WordBatch> existingAntonyms = findExistingWords(antonymWords);
                        List<WordBatch> newAntonyms = createPlaceholderWords(antonymWords, existingAntonyms, language);

                        List<WordBatch> allAntonyms = new ArrayList<>();
                        allAntonyms.addAll(existingAntonyms);
                        allAntonyms.addAll(newAntonyms);

                        Set<RelationBatch> antonymRelations = createWordRelations(definitionBatch, allAntonyms, RelationEnumType.ANTONIMA);
                        definitionBatch.setAntonymRelations(antonymRelations);
                    }

                    definitions.add(definitionBatch);
                }
            }
            wordBatch.setDefinitions(definitions);
            return wordBatch;
        };
    }

    /**
     * Encuentra palabras existentes en la base de datos.
     */
    private List<WordBatch> findExistingWords(List<String> words) {
        List<WordBatch> existingWords = new ArrayList<>();
        for (String word : words) {
            WordBatch existingWord = existingWordsMap.get(word);
            if (existingWord != null) {
                existingWords.add(existingWord);
            }
        }
        return existingWords;
    }

    /**
     * Crea palabras placeholder para aquellas que no existen en la base de datos.
     */
    private List<WordBatch> createPlaceholderWords(List<String> allWords, List<WordBatch> existingWords, LanguageModel language) {
        // Obtener una lista de palabras que no existen en la base de datos
        Set<String> existingWordTexts = existingWords.stream()
                .map(WordBatch::getWord)
                .collect(Collectors.toSet());

        List<String> missingWords = allWords.stream()
                .filter(word -> !existingWordTexts.contains(word))
                .collect(Collectors.toList());

        List<WordBatch> newWords = new ArrayList<>();
        for (String missingWord : missingWords) {
            WordBatch newWord = new WordBatch();
            newWord.setWord(missingWord);
            newWord.setLength(missingWord.length());
            newWord.setLanguage(language);
            newWord.setPlaceholder(true); // Marcar como placeholder

            newWord = wordBatchRepository.save(newWord); // Guardar en la base de datos
            existingWordsMap.put(missingWord, newWord); // Actualizar el mapa en memoria
            newWords.add(newWord);
        }

        return newWords;
    }

    /**
     * Crea relaciones entre la definición y las palabras relacionadas.
     */
    private Set<RelationBatch> createWordRelations(DefinitionBatch definitionBatch, List<WordBatch> relatedWords, RelationEnumType relationType) {
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

    // Step0: Cargar los idiomas en un Map
    @Bean
    public Step getLanguagesListStep() {
        return new StepBuilder("step0", jobRepository)
                .<LanguageModel, LanguageModel>chunk(100, transactionManager)
                .reader(languageReader())
                .writer(languageWriter())
                .build();
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
        };
    }

    // Step1: Escribir las palabras en la BBDD
    @Bean
    public Step addWordStep() {
        return new StepBuilder("wordBatchStep", jobRepository)
                .<WordBatchDTO, WordBatch>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(wordProcessor())
                .writer(filteredWordWriter())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("wordJob", jobRepository)
                .start(getLanguagesListStep()) // Obtiene listado de idiomas
                .next(getQualificationsListStep()) // Obtiene las qualifications existentes en la BBDD
                .next(addWordStep()) // Añade la palabra a la BBDD
                .build();
    }
}