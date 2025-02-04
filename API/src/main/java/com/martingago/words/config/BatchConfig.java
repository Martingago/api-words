package com.martingago.words.config;

import com.martingago.words.batch.WordItemProcessor;
import com.martingago.words.batch.WordItemReader;
import com.martingago.words.batch.WordItemWriter;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.WordModel;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Configuration
public class BatchConfig {


    @Bean
    @StepScope
    public WordItemReader itemReader(@Value("#{jobParameters['inputFile']}") File fileJson) {
        return new WordItemReader(fileJson);
    }

    @Bean
    public WordItemProcessor itemProcessor(){
        return  new WordItemProcessor();
    }

    @Bean
    public WordItemWriter wordItemWriter(){
        return  new WordItemWriter();
    }

    @Bean
    public Step wordImportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            WordItemReader reader,
            WordItemProcessor processor,
            WordItemWriter writer
    ) {
        return new StepBuilder("wordImportStep", jobRepository)
                .<WordResponseDTO, WordModel>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public Job wordImportJob(JobRepository jobRepository, Step wordImportStep) {
        return new JobBuilder("wordImportJob", jobRepository)
                .start(wordImportStep)
                .build();
    }

}
