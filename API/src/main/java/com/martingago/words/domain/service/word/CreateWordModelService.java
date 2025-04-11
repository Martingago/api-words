package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.*;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CreateWordModelService {

    /**
     * Crea la entidad que se va a persistir en la base de datos de WordModel
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @return objeto persistido en la BDDD.
     */
    public WordModel createWord(WordBatchDTO dto, LanguageModel languageModel) {
        WordModel wordModel = new WordModel();
        wordModel.setWord(dto.getWord());
        wordModel.setLength(dto.getLength());
        wordModel.setPlaceholder(false);

        if (languageModel == null) {
            return null;
        }
        wordModel.setLanguageModel(languageModel);
        return wordModel;
    }




}
