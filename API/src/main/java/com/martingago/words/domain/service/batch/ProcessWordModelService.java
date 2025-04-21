package com.martingago.words.domain.service.batch;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.domain.service.qualification.WordQualificationService;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.models.word.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.WordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProcessWordModelService {

    private final LanguageService languageService;
    private final WordQualificationService wordQualificationService;
    private final WordService wordService;
    private final CreateWordModelService createWordModelService;

    /**
     * Función que recibiendo un WordDTO individual se encarga de gestionar los idiomas, qualificaciones
     * y palabras a persistir de forma automática.
     * @param wordDTO Objeto DTO que se quiere convertir en un WordModel listo para añadir a la base de datos.
     * @return WordModel listo para añadir en la base de datos.
     */
    public WordModel processWordDTO(WordDTO wordDTO){
        Map<String, LanguageModel> languageModelMap = languageService.getAllLanguagesMappedByLangCode(); //Obtiene información de los idiomas de la base de datos.
        Map<String, WordQualificationModel> wordQualificationModelMap = wordQualificationService.getAllQualificationsMapped(); //Obtiene información de las qualifications de la base de datos.
        Map<String, WordModel> newWordsModelToPersist = new HashMap<>(); //Instancia un map de palabras relacionadas que van a ser persistidas
        Map<String, SimpleWordSerializableDTO> existingDBWordsMap = wordService.findReferencesFromWordDTO(wordDTO); //Busca palabras relacionadas existentes en la Base de datos.

        //Crea la entidad WordModel que se quiere guardar en la base de datos.
        return createWordModelService.processWordDTOintoWordModel(wordDTO,
                languageModelMap,
                wordQualificationModelMap,
                newWordsModelToPersist,
                existingDBWordsMap);
    }
}
