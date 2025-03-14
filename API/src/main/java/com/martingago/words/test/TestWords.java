package com.martingago.words.test;

import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.utils.BatchUtils;
import com.martingago.words.utils.JsonValidation;
import com.netflix.discovery.converters.Auto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class TestWords {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    WordTestRepository wordTestRepository;

    @Autowired
    BatchUtilsTest batchUtilsTest;

    @Autowired
    LanguageService languageService;

    @Transactional
    public void processAllJsonData(Map<String, WordResponseViewDTO> allWords) {
        int totalWords = allWords.size();
        final int[] processedWords = {0};

        LanguageModel languageModel = languageService.searchLanguageByLangCode("esp");

        // Procesa todo el lote en lotes más pequeños dentro de una única transacción empleando la funcion que integra su entityManager
        batchUtilsTest.processMapInBatches(allWords, 50, batch -> {
            try {

                // Procesar el batch sin transacción adicional
                insertBatchedWordToDatabase(batch, languageModel);

                // Log del progreso
                processedWords[0] += batch.size();
                log.info("Processed batch: {} words inserted (Total processed: {}/{})",
                        batch.size(), processedWords[0], totalWords);
            } catch (Exception e) {
                // Manejo de errores
                log.error("Error processing batch: {}", e.getMessage(), e);
                // No hacemos throw de la excepción para permitir que continúe con otros lotes
            }
        });
        log.info("Finished processing {} words out of {}", processedWords[0], totalWords);
    }

    //Funcion que añade un lote de operaciones bajo una unica transaccion
    public void insertBatchedWordToDatabase(Map<String, WordResponseViewDTO> batchedWords, LanguageModel languageModel){

        List<WordModelTest> wordModelList = new ArrayList<>();
        batchedWords.values().forEach(wordDto -> {
            WordModelTest wordModel = WordModelTest.builder()
                    .languageModel(languageModel)
                    .wordLength(wordDto.getLength())
                    .word(wordDto.getWord())
                    .isPlaceholder(false)
                    .build();
            wordModelList.add(wordModel);
        });
        wordTestRepository.saveAll(wordModelList);
        entityManager.flush();
        entityManager.clear();
    }

}

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words_test")
class WordModelTest{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_entity_seq")
    @SequenceGenerator(name = "word_entity_seq", sequenceName = "word_entity_seq", allocationSize = 50)
    private long id;

    @Column(name = "word"
            //, unique = true
    )
    private String word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language", nullable = false)
    private LanguageModel languageModel; //Idioma al que está asociada la palabra

    private int wordLength; //Longitud de la palabra

    private boolean isPlaceholder;
}

@Repository
interface WordTestRepository extends JpaRepository<WordModelTest, Long> {}

@RestController
@RequestMapping("api/v1/")
class TesWordsController{

    @Autowired
    JsonValidation jsonValidation;

    @Autowired
    TestWords testWords;

    @PostMapping("test/words")
    public void addWordList(@RequestParam("files") MultipartFile file){
        try {
            Map<String, WordResponseViewDTO> words = jsonValidation.parseFileToWordMap(file);
            testWords.processAllJsonData(words);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
