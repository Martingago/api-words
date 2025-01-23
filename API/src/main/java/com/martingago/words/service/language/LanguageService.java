package com.martingago.words.service.language;

import com.martingago.words.model.LanguageModel;
import com.martingago.words.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    public LanguageModel searchLanguageByLangCode(String langCode){
        return languageRepository.findByLangCode(langCode)
                .orElseThrow(() ->
                        new EntityNotFoundException("Language with lang code '" + langCode + "' was not founded"));
    }
}
