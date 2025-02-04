package com.martingago.words.controller;

import com.martingago.words.service.WordImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/words")
public class WordBatchController {

    @Autowired
    private WordImportService wordImportService;


    @PostMapping("/import")
    public ResponseEntity<String> importWords(@RequestParam("file") MultipartFile file) {
        try {
            wordImportService.importWords(file);
            return ResponseEntity.ok("File uploaded and processed successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }
}
