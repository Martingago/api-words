package com.martingago.words.batch;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.language.LanguageService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class WordItemProcessor implements ItemProcessor<WordResponseDTO, WordModel> {

    @Autowired
    LanguageService languageService;

    @Override
    public WordModel process(WordResponseDTO dto) {
        //Sustiuirlo por directamente el codigo de idioma
        LanguageModel languageModel = languageService.searchLanguageByLangCode(dto.getLanguage());
        return WordModel.builder()
                .word(dto.getWord())
                .languageModel(languageModel)
                .wordLength(dto.getLength())
                .build();
    }
}