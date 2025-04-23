package com.martingago.words.controller.words.search;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.FilterWordsService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
public class SearchWordFilterController {

    private final FilterWordsService filterWordsService;

    @GetMapping("/words/filter")
    public ResponseEntity<ApiResponseDTO<List<String>>> filterWords(
            @Parameter(description = "Filtrar palabras que empiecen por este valor.", example = "a")
            @RequestParam(required = false) String startsWith,

            @Parameter(description = "Filtrar palabras que terminen por este valor.", example = "r")
            @RequestParam(required = false) String endsWith,

            @Parameter(description = "Filtrar palabras con longitud exacta.", example = "5")
            @RequestParam(required = false) Integer length,

            @Parameter(description = "Filtrar palabras por código de idioma.", example = "esp")
            @RequestParam(required = false) String langCode,

            @Parameter(description = "Filtrar palabras por una lista de calificaciones.", example = "sustantivo masculino")
            @RequestParam(required = false) List<String> qualifications
    ){
        List<WordModel> filteredWords = filterWordsService.getWordsExtendedFilters(startsWith,endsWith,length,langCode, qualifications);
        List<String> responseDTOList = filteredWords.stream()
                .map(WordModel::getWord)
                .collect(Collectors.toList());

        return ApiResponseDTO.build(true,
                "Filtered words found successfully",
                HttpStatus.OK.value(),
                responseDTOList,
                HttpStatus.OK);
    }
}
