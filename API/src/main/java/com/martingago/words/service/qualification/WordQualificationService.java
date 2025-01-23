package com.martingago.words.service.qualification;

import com.martingago.words.dto.WordQualificationDTO;
import com.martingago.words.mapper.WordQualificationMapper;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordQualificationService {

    @Autowired
    WordQualificationRepository wordQualificationRepository;

    @Autowired
    WordQualificationMapper wordQualificationMapper;

    private WordQualificationModel searchQualificationByName(String qualification){
        return wordQualificationRepository.findByQualification(qualification)
                .orElseThrow(() -> new EntityNotFoundException("Qualification: '" + qualification + "' was not founded"));
    }

    private  WordQualificationModel insertQualificationData(WordQualificationDTO wordQualificationDTO){
        return wordQualificationRepository.save(
                wordQualificationMapper.toEntity(wordQualificationDTO)
        );
    }

    public WordQualificationModel validateAndInsertQualification(WordQualificationDTO wordQualificationDTO){
        try{
            return searchQualificationByName(wordQualificationDTO.getQualification());
        }catch (EntityNotFoundException e){
            return  insertQualificationData(wordQualificationDTO);
        }
    }
}
