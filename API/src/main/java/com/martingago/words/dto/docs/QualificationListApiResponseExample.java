package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.qualification.WordQualificationDTO;

import java.util.List;

public class QualificationListApiResponseExample extends ApiResponseDTO<List<WordQualificationDTO>> {

    protected QualificationListApiResponseExample(boolean status, String message, int serverCode, List<WordQualificationDTO> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}
