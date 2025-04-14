package com.martingago.words.domain.repository;

import com.martingago.words.domain.model.WordDefinitionModel;
import com.martingago.words.domain.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WordDefinitionRepository  extends JpaRepository<WordDefinitionModel, Long> {

}
