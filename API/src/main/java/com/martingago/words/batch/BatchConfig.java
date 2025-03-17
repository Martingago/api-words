package com.martingago.words.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.JsonLineMapper;

import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    @Bean
    public JsonItemReader<WordBatch> jsonLineItemReader() {
        return new JsonItemReaderBuilder<WordBatch>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(WordBatch.class))
                .resource(new ClassPathResource("data.jsonl"))
                .name("wordBatchJsonItemReader")
                .build();
    }


}
