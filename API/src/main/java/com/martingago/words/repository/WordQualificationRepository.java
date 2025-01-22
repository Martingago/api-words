package com.martingago.words.repository;

import com.martingago.words.model.WordQualificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordQualificationRepository extends JpaRepository<WordQualificationModel, Long> {

    Optional<WordQualificationModel> findByQualification(String qualification);
}
