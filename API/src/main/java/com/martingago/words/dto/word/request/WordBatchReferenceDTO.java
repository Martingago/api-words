package com.martingago.words.dto.word.request;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WordBatchReferenceDTO implements Serializable {
    private long id;
    private String word;
    private boolean isPlaceholder;
    private long languageId;

}
