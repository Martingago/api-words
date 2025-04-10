package com.martingago.words.context;

import com.martingago.words.domain.model.LanguageModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WordPojo {

    private String word;
    private LanguageModel languageModel;
}
