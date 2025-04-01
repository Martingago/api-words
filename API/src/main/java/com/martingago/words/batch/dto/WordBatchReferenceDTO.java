package com.martingago.words.batch.dto;

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
