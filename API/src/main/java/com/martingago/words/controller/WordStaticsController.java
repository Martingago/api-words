package com.martingago.words.controller;

import com.martingago.words.dto.global.ApiResponse;
import com.martingago.words.dto.global.WordStaticsDTO;
import com.martingago.words.service.global.WordsStaticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class WordStaticsController {

    @Autowired
    WordsStaticsService wordsStaticsService;


    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<WordStaticsDTO>> getWordsStatics(){
        WordStaticsDTO statics = wordsStaticsService.getWordsStatics();
        return ApiResponse.build(
                true,
                "Stats successfully founded",
                HttpStatus.OK.value(),
                statics,
                HttpStatus.OK);
    }
}
