package com.martingago.words.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BatchUtils {

    /**
     * Procesa un conjunto de entidades en lotes más pequeños.
     *
     * @param entities          Conjunto de entidades a procesar.
     * @param batchSize         Tamaño máximo de cada lote.
     * @param batchProcessor    Función que procesa cada lote de entidades.
     * @param <T>               Tipo de las entidades.
     */
    public static <T> void processInBatches(Set<T> entities, int batchSize, Consumer<Set<T>> batchProcessor) {
        // Convertir el conjunto de entidades en una lista para facilitar el manejo de índices.
        List<T> entitiesList = new ArrayList<>(entities);

        // Recorrer la lista en pasos de "batchSize".
        for (int i = 0; i < entitiesList.size(); i += batchSize) {
            // Calcular el índice final del lote actual.
            int end = Math.min(i + batchSize, entitiesList.size());

            // Extraer el subconjunto de entidades que forman el lote actual.
            Set<T> batch = new HashSet<>(entitiesList.subList(i, end));

            // Procesar el lote actual utilizando la función proporcionada.
            batchProcessor.accept(batch);
        }
    }
}
