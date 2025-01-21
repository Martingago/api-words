package com.martingago.words.service;

import com.martingago.words.dto.WordDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    WordMapper wordMapper;


    public WordDTO getWordByName(String word){
        WordModel wordModel = wordRepository.findByWordWithRelations(word).orElseThrow( () -> new EntityNotFoundException("Word " + word + " was not founded on database"));
        return wordMapper.toDTO(wordModel);
    }


    /**
     * Generates a random word to send to users as a secrect word to discover.
     *
     * @return WordResponseDTO
     */
//    public WordDTO generateRandomWord() {
//        WordModel wordModel = wordRepository.findRandomWord();
//        return WordMapper.toDTO(wordModel);
//    }

    /**
     * Adds a new single word to the API database
     *
     * @param wordDTO DTO of the word that gona be added
     * @return the updated word
     */


    /**
     * Using an upload CSV insert the values on the database with batch processing
     *
     * @param file
     * @return
     */
    public String uploadWordsCSV(MultipartFile file) {
        int totalCount = 0;
        int invalidCount = 0;
        int updateCount = 0;
        int duplicateCount = 0;
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            //Reads the file and all the records on it
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("word", "status", "qualification", "definition", "length", "language")
                    .withSkipHeaderRecord()
                    .parse(reader);

            List<WordModel> batchList = new ArrayList<>();
            List<String> wordsToCheck = new ArrayList<>();
            Map<String, CSVRecord> recordMap = new HashMap<>();

            // First, collect all valid words that need checking
            for (CSVRecord record : records) {
                totalCount++;
                if (!Boolean.parseBoolean(record.get("status"))) {
                    System.out.println("Skipping: '" + record.get("word") + "' not valid word");
                    invalidCount++;
                    continue;
                }
                wordsToCheck.add(record.get("word"));
                recordMap.put(record.get("word"), record);
            }

            // Batch check existence of words
            List<String> existingWords = wordRepository.findExistingWords(wordsToCheck);
            Set<String> existingWordsSet = new HashSet<>(existingWords);

            // Process records that don't exist
            for (String word : wordsToCheck) {
                if (existingWordsSet.contains(word)) {
                    System.out.println("Skipping: '" + word + "' duplicate word");
                    duplicateCount++;
                    continue;
                }

                CSVRecord record = recordMap.get(word);
                try {
                    updateCount++;

                    if (batchList.size() >= 100) {
                        wordRepository.saveAll(batchList);
                        batchList.clear();
                    }
                } catch (NumberFormatException e) {
                    System.err.println(word + " has an error with conversion to int of the column 'length', value is not a valid number");
                }
            }

            // Save any remaining items in the batch
            if (!batchList.isEmpty()) {
                wordRepository.saveAll(batchList);
            }

        } catch (Exception e) {
            System.err.println("Exception reading CSV words: " + e.getMessage());
        }
        return "Successfully handled " + totalCount + " words. " +
                "Updated: " + updateCount + " words. (" + duplicateCount +
                " duplicated words and " + invalidCount+" invalid words)";
    }

    /**
     * Delete words in batch from a CSV file
     *
     * @param file CSV file containing words to delete
     * @return String with deletion result
     */
    public String deleteWordsFromCSV(MultipartFile file) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

            // Read the CSV file
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("word")
                    .withSkipHeaderRecord()
                    .parse(reader);

            List<String> wordsToDelete = new ArrayList<>();
            for (CSVRecord record : records) {
                wordsToDelete.add(record.get("word"));
            }

            // Delete all words in a single operation
            if (!wordsToDelete.isEmpty()) {
                wordRepository.deleteAllByWordIn(wordsToDelete);
            }

            return "Processed deletion for " + wordsToDelete.size() + " words";

        } catch (Exception e) {
            System.err.println("Exception deleting words from CSV: " + e.getMessage());
            return "Error processing CSV file: " + e.getMessage();
        }
    }
}
