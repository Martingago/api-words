package com.martingago.words.domain.service.qualification;

import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.repository.models.WordQualificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WordQualificationService {

    private final WordQualificationRepository wordQualificationRepository;

    /**
     * Función que obtiene la información de todas las Qualification existentes en la Base de datos y lo devuelve
     * en un Map, cuya clave es la Qualification y su Objeto la entidad correspondiente en la BBDD.
     * @return Map <String, WordDefinitionModel>
     */
    public Map<String, WordQualificationModel> getAllQualificationsMapped(){
        List<WordQualificationModel> wordQualificationModelList =  wordQualificationRepository.findAll();
        if (wordQualificationModelList.isEmpty()) {
            return new HashMap<>();
        }
        return wordQualificationModelList.stream()
                .collect(Collectors.toMap(WordQualificationModel::getQualification, qualification -> qualification));
    }


}
