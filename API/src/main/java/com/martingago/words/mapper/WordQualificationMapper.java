package com.martingago.words.mapper;

import com.martingago.words.dto.WordQualificationDTO;
import com.martingago.words.model.WordQualificationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordQualificationMapper {

    /**
     * Genera un WordQualificationDTO a partir de un WordQualificationModel
     * @param wordQualificationModel
     * @return
     */
    public WordQualificationDTO toDTO(WordQualificationModel wordQualificationModel){
        if(wordQualificationModel == null) return  null;
        return new WordQualificationDTO(wordQualificationModel.getQualification());
    }

    /**
     * Genera un WordQualificationModel a partir de un WordQualificationDTO
     * @param wordQualificationDTO
     * @return
     */
    public WordQualificationModel toEntity(WordQualificationDTO wordQualificationDTO){
        return WordQualificationModel.builder()
                .qualification(wordQualificationDTO.getQualification())
                .build();
    }

}
