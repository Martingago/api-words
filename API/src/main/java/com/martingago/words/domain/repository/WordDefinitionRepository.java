package com.martingago.words.domain.repository;

import com.martingago.words.domain.model.WordDefinitionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordDefinitionRepository  extends JpaRepository<WordDefinitionModel, Long> {
}
