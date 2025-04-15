package com.martingago.words.dto.models.relation;

import com.martingago.words.domain.model.RelationEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que indica la relación de una palabra con otra palabra.")
public class WordRelationDTO {

    @Schema(description = "Tipo de relación existente con otra palabra.")
    private RelationEnumType relationType; //Tipo de relación con la palabra.

    @Schema(description = "Palabra con la que mantiene la relación.")
    private String relatedWord; //String de la palabra con la que tiene relación.
}
