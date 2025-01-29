package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class BatchInsertionQualificationService {

    @Autowired
    WordQualificationRepository wordQualificationRepository;

    public Map<String, WordQualificationModel> insertBatchWordDefinitionsMap(
            Map<String, WordListDefinitionsPojo> stringWordListDefinitionsPojoMap,
            Map<String, WordQualificationModel> mappedQualifications) {

        // Mapa para almacenar las qualifications que se van a insertar
        Map<String, WordQualificationModel> insertedQualificationsMap = new HashMap<>();

        // Procesamos las qualifications en lotes de 50
        BatchUtils.processMapInBatches(stringWordListDefinitionsPojoMap, 50, batch -> {

            try {
                //Set que contiene la información de las qualifications que vamos a insertar
                Set<WordQualificationModel> qualificationModels = new HashSet<>();

                // Por cada entrada del lote, recorremos las definiciones asociadas a la palabra
                for (Map.Entry<String, WordListDefinitionsPojo> entry : batch.entrySet()) {
                    WordListDefinitionsPojo pojo = entry.getValue();

                    // Recorremos las definiciones de la palabra
                    for (var definitionDTO : pojo.getWordDefinitionDTOSet()) {
                        String qualification = definitionDTO.getQualification();

                        // Si la qualification ya existe en la BBDD, no la insertamos
                        if (mappedQualifications.containsKey(qualification)) {
                            continue;
                        }

                        // Si no existe, creamos el modelo de qualification y lo agregamos al set
                        WordQualificationModel qualificationModel = WordQualificationModel.builder()
                                .qualification(qualification)
                                .build();

                        // Añadimos la qualification al set para insertar
                        qualificationModels.add(qualificationModel);

                        //Se añade la qualification al map para evitar que trate de insertarla numerosas veces
                        mappedQualifications.put(qualification, qualificationModel);
                    }
                }

                // Realizamos la inserción en la base de datos
                if(!qualificationModels.isEmpty()){
                    Set<WordQualificationModel> savedQualifications = new HashSet<>(wordQualificationRepository.saveAll(qualificationModels));

                    // Actualizamos el mapa con las qualifications insertadas
                    for (WordQualificationModel savedQualification : savedQualifications) {
                        insertedQualificationsMap.put(savedQualification.getQualification(), savedQualification);
                    }
                }

            }catch (Exception e){
                log.error("Error processing qualifications batch: {}", e.getMessage(), e);
            }
        });

        return insertedQualificationsMap;

    }
}
