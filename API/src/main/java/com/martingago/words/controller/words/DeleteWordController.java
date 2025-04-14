package com.martingago.words.controller.words;

import com.martingago.words.domain.model.LanguageModel;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.DeleteWordRequestDTO;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.language.LanguageService;
import com.martingago.words.domain.service.word.WordService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Hidden
public class DeleteWordController {

    private final WordService wordService;

    /**
     * Elimina una palabra bajo un string específico
     * @param deleteWordRequestDTO
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseDTO<Object>> deleteWordByString(
            @RequestBody @Valid DeleteWordRequestDTO deleteWordRequestDTO) {

        //Compobar que la palabra en el idioma indicado exista en la BBDD
        WordModel wordToDelete = wordService.searchBasicWordWithLanguage(deleteWordRequestDTO.getWord(), deleteWordRequestDTO.getLangCode());

        //Eliminar la palabra de la base de datos
        wordService.deleteWordByWordModel(wordToDelete);

        //Si no se captura ningún error se crea la salida correspondiente
        return ApiResponseDTO.build(true,
                "Word: '" + deleteWordRequestDTO.getWord() + "' successfully deleted",
                HttpStatus.OK.value(),
                deleteWordRequestDTO,
                HttpStatus.OK);
    }
}
