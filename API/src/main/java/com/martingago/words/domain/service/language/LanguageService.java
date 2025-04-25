package com.martingago.words.domain.service.language;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.repository.models.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    /**
     * Obtiene un listado con todos los idiomas existentes en la base de datos.
     * @return List con los LanguageModel encontrados
     */
    public List<LanguageModel> getLanguagesFromDatabase(){
        return  languageRepository.findAll();
    }

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
