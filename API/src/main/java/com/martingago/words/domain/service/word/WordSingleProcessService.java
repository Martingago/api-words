package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.*;
import com.martingago.words.domain.repository.WordQualificationRepository;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordSingleProcessService {

    private final WordQualificationRepository qualificationRepository;
    private final CreateWordModelService createWordModelService;

}

