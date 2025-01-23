package com.martingago.words.repository;

import com.martingago.words.model.WordQualificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WordQualificationRepository extends JpaRepository<WordQualificationModel, Long> {

    Optional<WordQualificationModel> findByQualification(String qualification);

    Set<WordQualificationModel> findByQualificationIn(Set<String> qualificationStringSet);

}
