package com.martingago.words.repository;

import com.martingago.words.model.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<LanguageModel, Long> {

    Optional<LanguageModel> findByLangCode(String code);
}
