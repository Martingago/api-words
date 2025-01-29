package com.martingago.words.service.qualification;

import com.martingago.words.dto.WordQualificationDTO;
import com.martingago.words.mapper.WordQualificationMapper;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * Recibe un listado de qualifications para validar y devuelve un map con las WordQualificationModel que se corresponden
     * @param qualifications set<String> con las qualifications a tratar
     * @return devuelve un map<String, WordQualificationModel> de las qualifications que tiene una palabra
     */
    public Map<String, WordQualificationModel> validateAndInsertQualifications(Set<String> qualifications){
        //Busca las qualifications existentes en una única consulta:
        Set<WordQualificationModel> existingQualifications = wordQualificationRepository.findByQualificationIn(qualifications);

        //Crea un map de las qualifications existentes para facilitar la búsqueda
        Map<String, WordQualificationModel> qualificationMap = existingQualifications.stream()
                .collect(Collectors.toMap(WordQualificationModel::getQualification, Function.identity())
                );

        //Identificar aquellas qualifications que no existen:
        Set<WordQualificationModel> missingQualifications = qualifications.stream()
                .filter(qualification -> !qualificationMap.containsKey(qualification))
                .map(qualificationName -> WordQualificationModel.builder()
                        .qualification(qualificationName)
                        .build()
                ).collect(Collectors.toSet());

        if(!missingQualifications.isEmpty()){
            //Inserta aquellas qualifications faltantes
            Set<WordQualificationModel> newQualifications = new HashSet<>(wordQualificationRepository.saveAll(missingQualifications));
            //Añade las nuevas qualifications al map de qualifications existentes:
            newQualifications.forEach(qualification -> qualificationMap.put(qualification.getQualification(), qualification));
        }
        //Devuelve todas las qualifications asociadas a las definiciones de una palabra.
        return qualificationMap;
    }

}
