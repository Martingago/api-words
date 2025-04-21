package com.martingago.words.domain.service.microservice;

import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.batch.ProcessWordModelService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;
import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.microservices.word.external.WordToScrapDTOExternal;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.exceptions.CustomExceptions;
import com.martingago.words.mapper.models.WordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WordMicroserviceService {

    private final MyScrapWordClient myScrapWordClient;
    private final WordMapper wordMapper;


    /**
     *
     * @param word
     * @return
     */
    public WordDTO findWordInMicroservice(String word){
        WordToScrapDTOExternal wordToScrapDTOExternal = new WordToScrapDTOExternal(word);

        // Usa el microservicio generado para buscar una palabra y devolver un WordDTO en caso de ser encontrada.
        ExternalBaseWordDTO externalBaseWordDTO = myScrapWordClient.procesarPalabra(wordToScrapDTOExternal);

        // Comprueba si lo que recibe del microservicio es una full o related word
        if (externalBaseWordDTO instanceof RelatedWordDTOExternal relatedWordResponse) {
            // Exception la palabra no ha podido ser procesada
            throw new CustomExceptions.WordRelatedGeneratedException("Couldn't find: " + wordToScrapDTOExternal.getWord() + " did you mean: " + relatedWordResponse.getRelatedWord());

        } else if (externalBaseWordDTO instanceof WordDTOExternal) {
            WordDTOExternal fullWordResponseDTO = (WordDTOExternal) externalBaseWordDTO;
            return wordMapper.toInternalDTO(fullWordResponseDTO);
        }else{
            //Si no recibe ninguna de las 2 opciones, estamos jodidos.
            return null;
        }
    }
}
