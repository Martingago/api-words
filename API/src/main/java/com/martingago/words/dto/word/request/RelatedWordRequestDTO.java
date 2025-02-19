package com.martingago.words.dto.word.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedWordResponseDTO extends  BaseWordResponseDTO{

    private String relatedWord;
}
