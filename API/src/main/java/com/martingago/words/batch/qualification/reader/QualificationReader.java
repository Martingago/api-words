package com.martingago.words.batch.qualification.reader;

import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordQualificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualificationReader {

    private final WordQualificationRepository wordQualificationRepository;

    public ItemReader<WordQualificationModel> read() {
        return new ListItemReader<>(wordQualificationRepository.findAll());
    }

}
