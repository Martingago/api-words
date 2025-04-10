package com.martingago.words.context;

import com.martingago.words.domain.model.WordDefinitionModel;
import com.martingago.words.domain.model.WordQualificationModel;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
public record DefinitionProcessedContext(
        Optional<WordQualificationModel> newQualification,
        WordDefinitionModel definitionModel
) {
    public boolean hasNewQualification() {
        return newQualification.isPresent();
    }
}
