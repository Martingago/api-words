package com.martingago.words.batch.language.writer;

import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;
import com.martingago.words.model.LanguageModel;
import org.springframework.batch.item.ItemWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LanguageWriter implements ItemWriter<LanguageModel> {

    private final WordBatchProcessor wordBatchProcessor;

    private Map<String, LanguageModel> languageMap;


    @Override
    public void write(Chunk<? extends LanguageModel> chunk) throws Exception {
        languageMap = new HashMap<>();
        for (LanguageModel language : chunk) {
            languageMap.put(language.getLangCode(), language);
        }
        wordBatchProcessor.setLanguageMap(languageMap);
        log.info("Idiomas guardados correctamente: " + languageMap.size());
    }
}
