package com.martingago.words.domain.repository.models;

import com.martingago.words.domain.model.WordExampleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordExampleRepository extends JpaRepository<WordExampleModel, Long> {
}
