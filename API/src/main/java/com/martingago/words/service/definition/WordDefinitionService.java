package com.martingago.words.service.definition;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.service.qualification.WordQualificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordDefinitionService {

    @Autowired
    WordDefinitionRepository wordDefinitionRepository;

    @Autowired
    WordQualificationService wordQualificationService;

    @Transactional
    public Set<WordDefinitionModel> validateAndInsertDefinitions(WordModel wordModel, Set<WordDefinitionDTO> wordDefinitionDTOSet) {
        // Extraer las qualifications de las definiciones recibidas
        Set<String> qualifications = wordDefinitionDTOSet.stream()
                .map(WordDefinitionDTO::getQualification)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Validar e insertar las qualifications
        Map<String, WordQualificationModel> qualificationModelMap = wordQualificationService.validateAndInsertQualifications(qualifications);

        // Crear las definiciones y asociar las qualifications
        Set<WordDefinitionModel> definitionsToSave = wordDefinitionDTOSet.stream()
                .map(definitionDTO -> WordDefinitionModel.builder()
                        .wordDefinition(definitionDTO.getDefinition())
                        .word(wordModel)
                        .wordQualificationModel(qualificationModelMap.get(definitionDTO.getQualification()))
                        .build())
                .collect(Collectors.toSet());

        // Guardar las definiciones en la base de datos y convertir a Set
        return new HashSet<>(wordDefinitionRepository.saveAll(definitionsToSave));
    }


}
