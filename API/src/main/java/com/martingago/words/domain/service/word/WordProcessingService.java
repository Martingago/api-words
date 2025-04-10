package com.martingago.words.domain.service.word;


import com.martingago.words.domain.repository.WordQualificationRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WordProcessingService {

    private final WordQualificationRepository wordQualificationRepository;
    private final EntityManager entityManager;

}
