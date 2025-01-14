package com.martingago.words.service;

import com.martingago.words.mapper.word.WordMapper;
import com.martingago.words.dto.WordResponseDTO;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class WordService {

    private final WordRepository wordRepository;

    @Autowired
    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * Generates a random word to send to users as a secrect word to discover.
     *
     * @return WordResponseDTO
     */
    public WordResponseDTO generateRandomWord() {
        WordModel wordModel = wordRepository.findRandomWord();
        return WordMapper.toDTO(wordModel);
    }

    /**
     * Adds a new single word to the API database
     *
     * @param wordResponseDTO DTO of the word that gona be added
     * @return the updated word
     */
    public WordResponseDTO addNewWord(WordResponseDTO wordResponseDTO) {
        if (wordRepository.existsByWord(wordResponseDTO.getWord())) {
            throw new DuplicateKeyException("Error, duplicate word on database: '" + wordResponseDTO.getWord() + "'");
        }
        try {
            WordModel wordModel = WordMapper.toModel(wordResponseDTO);
            WordModel wordSaved = wordRepository.save(wordModel);
            return WordMapper.toDTO(wordSaved);
        } catch (Exception e) {
            throw new RuntimeException("Error");
        }
    }

    /**
     * Using an upload CSV insert the values on the database
     *
     * @param file
     * @return
     */
    public String uploadWordsCSV(MultipartFile file) {
        int count = 0;
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            //Reads the file and all the records on it
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("word", "status", "calification", "definition", "length", "language")
                    .withSkipHeaderRecord()
                    .parse(reader);
            List<WordModel> batchList = new ArrayList<>();

            //Loads the records as DTO
            for (CSVRecord record : records) {
                //If word doesn't exist on db its loaded
                if (!Boolean.parseBoolean(record.get("status"))) {
                    System.out.println("Skipping: '" + record.get("word") + "' not valid word");
                    continue;
                }
                if (wordRepository.existsByWord(record.get("word"))) {
                    System.out.println("Skipping: '" + record.get("word") + "' duplicate word");
                    continue;
                }
                try {
                    WordResponseDTO wordResponseDTO = new WordResponseDTO(record.get("word"),
                            record.get("language"),
                            Integer.parseInt(record.get("length")),
                            record.get("description"));
                    WordModel wordModel = WordMapper.toModel(wordResponseDTO);
                    batchList.add(wordModel);
                    count++;
                    if (batchList.size() >= 100) {
                        wordRepository.saveAll(batchList);
                        batchList.clear();
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error with conversion to int of the column 'length', value is not a valid number");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception reading CSV words: " + e.getMessage());
        }
        return "successfully upload " + count + " words";
    }

    ;

}
