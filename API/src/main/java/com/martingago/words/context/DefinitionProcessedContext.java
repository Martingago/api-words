package com.martingago.words.context;

import com.martingago.words.domain.model.WordDefinitionModel;
import com.martingago.words.domain.model.WordQualificationModel;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Es una clase que se utiliza tras haber añadido una Definición a la base de datos, si dicha definición no tenía la qualification necesaria
 * se añade a la BBDD para posteriormente actualizar el map del batch.
 *
 * Esta clase se utiliza como intermediaria en esa funcion para obtener correctamente la información de los elementos añadidos en la BBDD.
 * @param newQualification Qualificación que fue añadida a la base de datos durante la inserción de definiciones
 * @param definitionModel definición que se ha añadido.
 */
@Builder
public record DefinitionProcessedContext(
        Optional<WordQualificationModel> newQualification,
        WordDefinitionModel definitionModel
) {
    public boolean hasNewQualification() {
        return newQualification.isPresent();
    }
}
