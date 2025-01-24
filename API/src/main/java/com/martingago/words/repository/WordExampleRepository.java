package com.martingago.words.repository;

import com.martingago.words.model.WordExampleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordExampleRepository extends JpaRepository<WordExampleModel, Long> {
}
