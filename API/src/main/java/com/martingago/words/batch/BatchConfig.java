package com.martingago.words.batch;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import com.martingago.words.batch.word.writer.FilteredWordBatchWriter;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.repository.LanguageRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final LanguageRepository languageRepository;
    private final WordBatchRepository wordBatchRepository;

    private Map<String, LanguageModel> languageMap;

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
     * @return
     */
    @Bean
    public ItemProcessor<WordBatchDTO, WordBatch> wordProcessor() {
        return dto -> {
            WordBatch wordBatch = new WordBatch();
            wordBatch.setWord(dto.getWord());
            wordBatch.setLength(dto.getLength());
            wordBatch.setPlaceholder(dto.isPlaceholder());

            // Buscar el LanguageModel en el Map
            LanguageModel language = languageMap.get(dto.getLanguage());
            if (language == null) {
                return null; //Si el idioma no existe se salta la palabra.
            }
            wordBatch.setLanguage(language);

            return wordBatch;
        };
    }

    // Step0: Cargar los idiomas en un Map
    @Bean
    public Step step0() {
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
    public Step wordBatchStep() {
        return new StepBuilder("wordBatchStep", jobRepository)
                .<WordBatchDTO, WordBatch>chunk(100, transactionManager) // Corrección del tipado
                .reader(itemReader())
                .processor(wordProcessor())
                .writer(filteredWordWriter())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("wordJob", jobRepository)
                .start(step0())
                .next(wordBatchStep())
                .build();
    }

}