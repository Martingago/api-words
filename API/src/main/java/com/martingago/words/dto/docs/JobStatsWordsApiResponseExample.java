package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.batch.JobStatsDTO;

public class JobStatsWordsApiResponseExample extends ApiResponseDTO<JobStatsDTO> {

    protected JobStatsWordsApiResponseExample(boolean status, String message, int serverCode, JobStatsDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }
}
