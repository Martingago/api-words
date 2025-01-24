package com.martingago.words.repository;

import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordQualificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordDefinitionRepository  extends JpaRepository<WordDefinitionModel, Long> {
}
