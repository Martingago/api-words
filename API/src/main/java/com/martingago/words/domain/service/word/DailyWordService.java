package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.models.WordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DailyWordService {

    private final WordRepository wordRepository;
    private Long dailyWordId;


    /**
     * Obtiene la palabra del día usando el ID almacenado en memoria.
     */
    public WordModel getDailyWord() {
        return wordRepository.findWordById(dailyWordId)
                .orElseThrow(() -> new RuntimeException("No se encontró la palabra del día"));
    }

    /**
     * Genera un nuevo ID de palabra aleatoria y lo almacena en memoria.
     */
    @Scheduled(cron = "0 0 0 * * *") // Se ejecuta todos los días a las 00:00
    //@Scheduled(cron = "0 * * * * *") // Se ejecuta cada minuto
    @PostConstruct // Se ejecuta cuando el bean es inicializado
    public void updateDailyWord() {
        this.dailyWordId = generateNewDailyWordId();
        System.out.println("GENERATED RANDOM DAILY WORD =========> " + this.dailyWordId);
    }

    /**
     * Obtiene el identificador de una palabra que fue obtenida de la base de datos de forma aleatoria
     * @return Long identificador de la palabra que se obtuvo aleatoriamente de la base de datos.
     */
    private Long generateNewDailyWordId() {
        return wordRepository.findRandomWordId(null);
    }
}

