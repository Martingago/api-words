package com.martingago.words.batch.qualification.writer;

import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import com.martingago.words.domain.model.WordQualificationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QualificationWriter implements ItemWriter<WordQualificationModel> {

    private final WordBatchProcessor wordBatchProcessor;

    private Map<String, WordQualificationModel> qualificationMap;

    @Override
    public void write(Chunk<? extends WordQualificationModel> chunk) throws Exception {
        qualificationMap = new HashMap<>();
        for (WordQualificationModel qualification : chunk) {
            qualificationMap.put(qualification.getQualification(), qualification);
        }
        //Se establece el qualification map en el processor
        wordBatchProcessor.setQualificationMap(qualificationMap);
    }
}
