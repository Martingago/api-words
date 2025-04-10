package com.martingago.words.context;

import com.martingago.words.domain.model.WordModel;
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
