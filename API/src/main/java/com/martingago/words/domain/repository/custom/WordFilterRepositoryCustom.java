package com.martingago.words.domain.repository.custom;

import com.martingago.words.domain.model.WordModel;
import java.util.List;

public interface WordFilterRepositoryCustom {
    List<WordModel> getWordsWithFilters(String startsWith, String endsWith, Integer length, String langCode);

    List<WordModel> getWordsWithExtendFilters(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications);
}

