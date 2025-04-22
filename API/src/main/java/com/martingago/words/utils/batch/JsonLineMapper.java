package com.martingago.words.utils.batch;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.file.LineMapper;

public class JsonLineMapper<T> implements LineMapper<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> targetType;

    public JsonLineMapper(Class<T> targetType) {
        this.objectMapper = new ObjectMapper();
        // Configurar ObjectMapper para ignorar propiedades desconocidas
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.targetType = targetType;
    }

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        // Convertir la línea JSON a un objeto del tipo especificado
        return objectMapper.readValue(line, targetType);
    }
}
