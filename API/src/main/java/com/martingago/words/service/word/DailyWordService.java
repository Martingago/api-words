package com.martingago.words.service.word;

import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyWordService {

    private final WordRepository wordRepository;
    private Long dailyWordId;

    public DailyWordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * Obtiene la palabra del día en base al ID almacenado en memoria.
     */
    public WordModel getDailyWord() {
        return wordRepository.findById(dailyWordId)
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

    private Long generateNewDailyWordId() {
        return wordRepository.findRandomWordId(null);
    }
}

