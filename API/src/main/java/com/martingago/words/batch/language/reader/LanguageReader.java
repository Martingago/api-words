package com.martingago.words.batch.language.reader;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageReader {

    private final LanguageRepository languageRepository;

    public ItemReader<LanguageModel> read() {
        return new ListItemReader<>(languageRepository.findAll());
    }
}