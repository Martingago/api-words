package com.martingago.words.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public FlatFileItemReader<WordBatch> jsonLineItemReader() {
        FlatFileItemReader<WordBatch> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("files/test.jsonl"));
        reader.setName("wordBatchJsonItemReader");

        // Configurar el LineMapper para JSONL
        DefaultLineMapper<WordBatch> lineMapper = new DefaultLineMapper<>();
        JsonLineMapper jsonLineMapper = new JsonLineMapper();
        jsonLineMapper.setObjectMapper(new ObjectMapper());
        lineMapper.setLineTokenizer(line -> line); // Cada línea es un objeto JSON completo
        lineMapper.setFieldSetMapper(jsonLineMapper); // Mapear la línea a WordBatch

        reader.setLineMapper(lineMapper);

        return reader;
    }


    @Bean
    public JpaItemWriter<WordBatch> wordWriter() {
        return new JpaItemWriterBuilder<WordBatch>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step wordBatchStep() {
        return new StepBuilder("wordBatchStep", jobRepository)
                .<WordBatch, WordBatch>chunk(10, transactionManager)
                .reader(jsonLineItemReader())
                .writer(wordWriter())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("wordJob", jobRepository)
                .start(wordBatchStep())
                .build();
    }
}
