package com.martingago.words.domain.repository.custom;

import com.martingago.words.domain.model.WordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordFilterRepositoryCustom {
    List<WordModel> getWordsWithFilters(String startsWith, String endsWith, Integer length, String langCode);

    List<WordModel> getWordsWithExtendFilters(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications);

    Page<WordModel> getWordsWithPagination(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable);
}

