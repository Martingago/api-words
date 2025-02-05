package com.martingago.words.config;

import com.martingago.words.batch.WordItemProcessor;
import com.martingago.words.batch.WordItemReader;
import com.martingago.words.batch.WordItemWriter;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.language.LanguageService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.stream.Collectors;


@Configuration
public class BatchConfig {

    @Autowired
    LanguageService languageService;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BatchConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public WordItemWriter wordItemWriter() {
        return new WordItemWriter(jdbcTemplate);
    }

        @Bean
    @StepScope
    public WordItemReader itemReader() {
        System.out.println("WordItemReader instance created!");
        return new WordItemReader();
    }

    @Bean
    @StepScope
    public WordItemProcessor itemProcessor() {
        return new WordItemProcessor();
    }

    @Bean
    public Step loadLanguagesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("loadLanguagesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Crear un map de lang_code a ID de idioma
                    Map<String, Long> languageIdMap = languageService.getAllLanguagesMappedByLangCode()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().getId()
                            ));

                    // Guardar en JobExecutionContext
                    contribution.getStepExecution().getJobExecution()
                            .getExecutionContext()
                            .put("languageIdMap", languageIdMap);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
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
    public Job wordImportJob(JobRepository jobRepository, Step loadLanguagesStep, Step wordImportStep) {
        return new JobBuilder("wordImportJob", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        // Podemos inicializar recursos si es necesario
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        // Limpieza después del job si es necesario
                    }
                })
                .start(loadLanguagesStep)
                .next(wordImportStep)
                .build();
    }


}
