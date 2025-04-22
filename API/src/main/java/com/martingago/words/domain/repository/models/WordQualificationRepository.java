package com.martingago.words.domain.repository.models;

import com.martingago.words.domain.model.WordQualificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface WordQualificationRepository extends JpaRepository<WordQualificationModel, Long> {

    Optional<WordQualificationModel> findByQualification(String qualification);

    Set<WordQualificationModel> findByQualificationIn(Set<String> qualificationStringSet);

}
