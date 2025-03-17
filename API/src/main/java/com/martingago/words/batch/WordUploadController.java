package com.martingago.words.batch;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.utils.JsonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class WordUploadController {

    @Autowired
    JsonValidation jsonValidation;

    @PostMapping("/upload-jsonl")
    public ResponseEntity<ApiResponseDTO<String>> uploadJsonlFile(@RequestParam("file") MultipartFile file) {
            if(jsonValidation.isValidJsonFile(file)){
                try{
                    Set<WordResponseViewDTO> wordSet = jsonValidation.parseJsonlFileToWordSet(file);
                    wordSet.forEach(System.out::println);
                    return ApiResponseDTO.build(
                            true,
                            "file upload",
                            HttpStatus.OK.value(),
                            "exito",
                            HttpStatus.OK);
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return ApiResponseDTO.build(
                        false,
                        "Error uploading file",
                        HttpStatus.BAD_REQUEST.value(),
                        "Fail",
                        HttpStatus.BAD_REQUEST
                );
            }

    }
}
