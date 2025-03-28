package com.martingago.words.batch;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.language.reader.LanguageReader;
import com.martingago.words.batch.language.writer.LanguageWriter;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.qualification.reader.QualificationReader;
import com.martingago.words.batch.qualification.writer.QualificationWriter;
import com.martingago.words.batch.word.ChunkCollectingItemReader;
import com.martingago.words.batch.word.ItemReadLogger;
import com.martingago.words.batch.word.WordChunkListener;
import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import com.martingago.words.batch.word.writer.FilteredWordBatchWriter;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordQualificationModel;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
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
    private final WordBatchProcessor wordBatchProcessor;
    private final LanguageReader languageReader; //Reader para los idiomas
    private final LanguageWriter languageWriter; //Writer para los idiomas
    private final QualificationReader qualificationReader; //Reader de las qualificaciones
    private final QualificationWriter qualificationWriter; //Writer de las qualificaciones
    private final ItemReadLogger itemReadLogger;


    @Bean
    public ItemStreamReader<WordBatchDTO> itemReader() {
        FlatFileItemReader<WordBatchDTO> baseReader = new FlatFileItemReader<>();
        baseReader.setResource(new ClassPathResource("files/small_test.jsonl"));
        baseReader.setLineMapper(new JsonLineMapper<>(WordBatchDTO.class));

        return new ChunkCollectingItemReader(baseReader);
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
        return new FilteredWordBatchWriter(jpaItemWriter());
    }


    /**
     * Paso para obtener las qualificaciones existentes en la aplicación.
     * @return
     */
    @Bean
    public Step getQualificationsListStep() {
        return new StepBuilder("step1", jobRepository)
                .<WordQualificationModel, WordQualificationModel>chunk(100, transactionManager)
                .reader(qualificationReader.read())
                .writer(qualificationWriter)
                .build();
    }

    /**
     * Paso para obtener la información de los idiomas que existen en la base de datos.
     * @return
     */
    @Bean
    public Step getLanguagesListStep() {
        return new StepBuilder("step0", jobRepository)
                .<LanguageModel, LanguageModel>chunk(100, transactionManager)
                .reader(languageReader.read())
                .writer(languageWriter)
                .build();
    }


    /**
     * Paso para añadir una palabra a la base de datos
     * @return
     */
    @Bean
    public Step addWordStep() {
        return new StepBuilder("wordBatchStep", jobRepository)
                .<WordBatchDTO, WordBatch>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(wordBatchProcessor)
                .writer(filteredWordWriter())
                .listener(itemReadLogger)
                .build();
    }

    /**
     * Job que obtiene la información de idiomas, las qualifications existentes, y con toda esa informacion, posteriormente
     * añade palabras a la BBDD
     * @return
     */
    @Bean
    public Job runJob() {
        return new JobBuilder("wordJob", jobRepository)
                .start(getLanguagesListStep())
                .next(getQualificationsListStep())
                .next(addWordStep())
                .build();
    }
}