package com.martingago.words.service.language;

import com.martingago.words.model.LanguageModel;
import com.martingago.words.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    public LanguageModel searchLanguageByLangCode(String langCode){
        return languageRepository.findByLangCode(langCode)
                .orElseThrow(() ->
                        new EntityNotFoundException("Language with lang code '" + langCode + "' was not founded"));
    }

    /**
     * Obtiene un map con los LanguageModels existentes en la BBDD.
     * @return map cuya clave es el lang_code y el objeto es el LanguageModel.
     */
    public Map<String, LanguageModel> getAllLanguagesMappedByLangCode(){
        List<LanguageModel> languageModelList = languageRepository.findAll();
        return languageModelList.stream()
                .collect(Collectors.toMap(LanguageModel::getLangCode, language -> language)
                );
    }
}
