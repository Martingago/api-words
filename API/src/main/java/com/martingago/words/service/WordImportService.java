package com.martingago.words.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class WordImportService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job wordImportJob;

    public void importWords(MultipartFile file) throws Exception {
        // Guardar el archivo temporalmente
        Path tempFile = Files.createTempFile("word-import-", ".json");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Crear parámetros para el job
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputFile", "file://" + tempFile.toAbsolutePath().toString())
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        // Ejecutar el job
        jobLauncher.run(wordImportJob, jobParameters);
    }
}