package com.martingago.words.dto.word.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelatedWordResponseDTO.class, name = "related"),
        @JsonSubTypes.Type(value = FullWordResponseDTO.class, name = "full")
})
public abstract class BaseWordResponseDTO{
    private String type;
}
