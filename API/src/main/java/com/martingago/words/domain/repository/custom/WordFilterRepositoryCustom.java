package com.martingago.words.domain.repository.custom;

import com.martingago.words.domain.model.WordModel;

import java.util.List;

public interface WordFilterRepositoryCustom {
    List<WordModel> findWordsWithFilters(String startsWith, String endsWith, Integer length, String langCode);
}

