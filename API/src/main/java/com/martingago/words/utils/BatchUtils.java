package com.martingago.words.utils;

import java.util.*;
import java.util.function.Consumer;

public class BatchUtils {

    /**
     * Procesa un conjunto de set entidades en lotes más pequeños.
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

    /**
     * Procesa un Map en lotes más pequeños.
     *
     * @param map            Mapa a procesar.
     * @param batchSize      Tamaño máximo de cada lote.
     * @param batchProcessor Función que procesa cada lote de datos.
     * @param <K>            Tipo de las claves en el Map.
     * @param <V>            Tipo de los valores en el Map.
     */
    public static <K, V> void processMapInBatches(Map<K, V> map, int batchSize, Consumer<Map<K, V>> batchProcessor) {
        // Convertimos el mapa en una lista de entradas (clave-valor)
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());

        // Recorrer la lista en pasos de "batchSize"
        for (int i = 0; i < entryList.size(); i += batchSize) {
            // Calcular el índice final del lote actual.
            int end = Math.min(i + batchSize, entryList.size());

            // Extraer el subconjunto de entradas que forman el lote actual.
            Map<K, V> batch = new HashMap<>();
            for (int j = i; j < end; j++) {
                Map.Entry<K, V> entry = entryList.get(j);
                batch.put(entry.getKey(), entry.getValue());
            }

            // Procesar el lote actual utilizando la función proporcionada.
            batchProcessor.accept(batch);
        }
    }


}
