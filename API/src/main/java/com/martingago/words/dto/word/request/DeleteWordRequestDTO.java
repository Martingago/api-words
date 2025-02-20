package com.martingago.words.dto.word.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class DeleteWordRequestDTO {
    @NonNull
    private String langCode;
    @NonNull
    private String word;
}
