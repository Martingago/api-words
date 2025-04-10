package com.martingago.words.domain.repository;

import com.martingago.words.domain.model.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<LanguageModel, Long> {

    Optional<LanguageModel> findByLangCode(String code);
}
