package com.martingago.words.domain.service.global;

import com.martingago.words.dto.global.stats.WordStatsDTO;
import com.martingago.words.domain.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WordsStaticsService {

    private final StatsRepository statsRepository;


    /**
     * Obtiene las estadísticas generales de palabras de la Base de datos.
     * @return WordStatsDTO objeto con las estadísticas de la base de datos.
     */
    public WordStatsDTO getWordsStatics(){
        return statsRepository.getStaticsFromDatabase();
    }
}
