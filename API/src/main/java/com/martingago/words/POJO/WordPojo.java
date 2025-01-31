package com.martingago.words.POJO;

import com.martingago.words.model.LanguageModel;
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
