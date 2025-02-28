package com.martingago.words.service.global;

import com.martingago.words.dto.global.stats.WordStatsDTO;
import com.martingago.words.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordsStaticsService {

    @Autowired
    StatsRepository statsRepository;


    /**
     * Obtiene las estadísticas generales de palabras de la Base de datos.
     * @return
     */
    public WordStatsDTO getWordsStatics(){
        return statsRepository.getStaticsFromDatabase();
    }
}
