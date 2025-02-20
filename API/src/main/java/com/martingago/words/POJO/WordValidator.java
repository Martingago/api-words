package com.martingago.words.POJO;

import com.martingago.words.model.WordModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordValidator {
    private boolean exists;
    private WordModel wordModel;
}
