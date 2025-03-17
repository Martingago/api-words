package com.martingago.words.batch;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.utils.JsonValidation;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WordUploadController {

    private final Job job;
    private final JobLauncher jobLauncher;

    @GetMapping("/validate-jsonl")
    public void validateJsonlFile(){
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // Asegura una ejecución única
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            System.out.println("Estado del Job: " + execution.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @PostMapping("/upload-jsonl")
//    public ResponseEntity<ApiResponseDTO<String>> uploadJsonlFile(@RequestParam("file") MultipartFile file) {
//            if(jsonValidation.isValidJsonFile(file)){
//                try{
//                    Set<WordResponseViewDTO> wordSet = jsonValidation.parseJsonlFileToWordSet(file);
//                    wordSet.forEach(System.out::println);
//                    return ApiResponseDTO.build(
//                            true,
//                            "file upload",
//                            HttpStatus.OK.value(),
//                            "exito",
//                            HttpStatus.OK);
//                }catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }else{
//                return ApiResponseDTO.build(
//                        false,
//                        "Error uploading file",
//                        HttpStatus.BAD_REQUEST.value(),
//                        "Fail",
//                        HttpStatus.BAD_REQUEST
//                );
//            }
//
//    }
}
