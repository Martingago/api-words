package com.martingago.words.controller.words;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordInsertionService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Hidden
public class AddWordController {

    private final WordInsertionService wordInsertionService;
    private final WordMapper wordMapper;

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
