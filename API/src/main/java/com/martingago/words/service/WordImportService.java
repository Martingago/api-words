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

@Service
public class WordImportService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job wordImportJob;

    public void importWords(MultipartFile file) throws IOException {
        // Convert MultipartFile to File
        File tempFile = convertMultipartFileToFile(file);

        try {
            // Create job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("inputFile", tempFile.getAbsolutePath())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Launch the job
            jobLauncher.run(wordImportJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException("Error processing batch job", e);
        } finally {
            // Clean up temporary file
            tempFile.delete();
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded-", ".json");
        file.transferTo(tempFile);
        return tempFile;
    }
}