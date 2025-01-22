package com.martingago.words.repository;

import com.martingago.words.model.WordRelationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordRelationRepository extends JpaRepository<WordRelationModel, Long> {

}
