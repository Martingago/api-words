package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.service.relation.WordRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class RelationController {

    @Autowired
    WordRelationService wordRelationService;

    /**
     * Controller que recibe como parámetro una palabra y realiza una búsqueda de sus sinónimos.
     * @param word
     * @return
     */
    @GetMapping("/synonyms/{word}")
    public ResponseEntity<ApiResponse<List<String>>> findSynonymsByWord(
            @PathVariable String word,
            @RequestParam(value = "lang", defaultValue = "esp") String langCode){
        List<String> listSynonyms = wordRelationService.getRelationTypeByWord(word, RelationEnumType.SINONIMA, langCode);
        return ApiResponse.build(
                true,
                "Synonyms founded ",
                HttpStatus.FOUND.value(),
                listSynonyms,
                HttpStatus.FOUND
        );

    }

    /**
     * Controller que recibe como parámetro una palabra y realiza una búsqueda de sus sinónimos.
     * @param word
     * @return
     */
    @GetMapping("/antonyms/{word}")
    public ResponseEntity<ApiResponse<List<String>>> findAntonymsByWord(
            @PathVariable String word,
            @RequestParam(value = "lang", defaultValue = "esp") String langCode
    ){
        List<String> listSynonyms = wordRelationService.getRelationTypeByWord(word, RelationEnumType.ANTONIMA, langCode);
        return ApiResponse.build(
                true,
                "Antonyms founded ",
                HttpStatus.FOUND.value(),
                listSynonyms,
                HttpStatus.FOUND
        );

    }

}
