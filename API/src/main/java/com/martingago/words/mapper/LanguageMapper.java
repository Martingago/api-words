package com.martingago.words.mapper;

import com.martingago.words.dto.LanguageDTO;
import com.martingago.words.model.LanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageMapper {

    /**
     * Genera un LanguageDTO que contiene únicamente el lang code.
     * @param languageModel
     * @return
     */
    public LanguageDTO toLangDTO(LanguageModel languageModel){
        return LanguageDTO.builder()
                .lang(languageModel.getLang_code())
                .build();
    }

    /**
     * Genera un LanguageDTO a partir de un languageModel
     * @param languageModel
     * @return
     */
    public LanguageDTO toDTO(LanguageModel languageModel){
        return LanguageDTO.builder()
                .language(languageModel.getLanguage())
                .lang(languageModel.getLang_code())
                .build();
    }

    /**
     * Genera una LanguageModel a partir de un languageDTO
     * @param languageDTO
     * @return
     */
    public LanguageModel toEntity(LanguageDTO languageDTO){
        return LanguageModel.builder()
                .language(languageDTO.getLanguage())
                .lang_code(languageDTO.getLang())
                .build();
    }
}
