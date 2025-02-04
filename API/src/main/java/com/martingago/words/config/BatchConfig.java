package com.martingago.words.config;

import com.martingago.words.batch.WordItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BatchConfig {

    @Bean
    public WordItemReader itemReader(String path){
        return new WordItemReader(path);
    }

}
