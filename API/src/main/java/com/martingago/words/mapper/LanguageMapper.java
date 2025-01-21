package com.martingago.words.mapper;

import com.martingago.words.dto.LanguageDTO;
import com.martingago.words.model.LanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageMapper {

    /**
     * Genera un LanguageDTO a partir de un LanguageModel
     * @param languageModel
     * @return
     */
    public LanguageDTO toDTO(LanguageModel languageModel){
        if(languageModel == null) return  null;
        return LanguageDTO.builder()
                .language(languageModel.getLanguage())
                .lang(languageModel.getLang_code())
                .build();
    }

    /**
     * Genera una LanguageModel a partir de un LanguageDTO
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
