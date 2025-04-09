package com.martingago.words.controller.words;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.DeleteWordRequestDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/private")
@Hidden
public class PrivateWordController {

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

    /**
     * Añade una palabra en la Base de datos.
     * @param fullWordResponseDTO
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> insertWord(
            @RequestBody @Valid FullWordRequestDTO fullWordResponseDTO){
        WordModel updatedWord= wordInsertionService.insertFullWord(fullWordResponseDTO);
        WordResponseViewDTO updatedWordResponseViewDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponseDTO.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseViewDTO,
                HttpStatus.CREATED);
    }


}
