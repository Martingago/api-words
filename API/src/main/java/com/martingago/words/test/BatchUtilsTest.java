package com.martingago.words.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class BatchUtilsTest {

    @PersistenceContext
    EntityManager entityManager;

    //@Transactional // Asegura que este método siempre tenga una transacción activa
    public <K, V> void processMapInBatches(Map<K, V> map, int batchSize, Consumer<Map<K, V>> batchProcessor) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());

        for (int i = 0; i < entryList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entryList.size());

            Map<K, V> batch = new HashMap<>();
            for (int j = i; j < end; j++) {
                Map.Entry<K, V> entry = entryList.get(j);
                batch.put(entry.getKey(), entry.getValue());
            }

            batchProcessor.accept(batch);

            // Mover flush y clear al método transaccional
            //entityManager.flush();
            //entityManager.clear();
        }
    }
}
