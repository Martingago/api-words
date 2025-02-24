package com.martingago.words.service.global;

import com.martingago.words.dto.global.WordStaticsDTO;
import com.martingago.words.repository.StaticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordsStaticsService {

    @Autowired
    StaticsRepository staticsRepository;


    /**
     * Obtiene las estadísticas generales de palabras de la Base de datos.
     * @return
     */
    public WordStaticsDTO getWordsStatics(){
        return staticsRepository.getStaticsFromDatabase();
    }
}
