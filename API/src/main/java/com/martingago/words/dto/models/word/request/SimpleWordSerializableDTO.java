package com.martingago.words.dto.models.word.request;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
/**
 * Objeto que se utiliza para serializar palabras encontradas en la base de datos y pasarlo entre steps de un job.
 * Contiene la información fundamental para conocer la información básica de una palabra y poder actualizarla en caso necesario.
 */
public class SimpleWordSerializableDTO implements Serializable {
    private long id;
    private String word;
    private boolean isPlaceholder;
}
