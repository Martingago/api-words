package com.martingago.words.mapper;

import com.martingago.words.dto.WordExampleDTO;
import com.martingago.words.model.WordExampleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WordExamplesMapper {

    /**
     * Genera un WordExampleDTO a partir del WordExampleModel
     * @param wordExampleModel
     * @return
     */
    public WordExampleDTO toDTO(WordExampleModel wordExampleModel){
        if(wordExampleModel == null) return null;
        return new WordExampleDTO(
                wordExampleModel.getExample()
        );
    }

    /**
     * Mapea una lista de WordExampleModel y lo convierte en una lista de WordExampleDTO (Strings)
     * @param wordExampleModelList
     * @return
     */
    public List<WordExampleDTO> toDTOList(List<WordExampleModel> wordExampleModelList){
        if(wordExampleModelList == null) return List.of();
        return wordExampleModelList.
                stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una lista de WordExampleModel y lo convierte en una lista de Strings
     * @param wordExampleModelList
     * @return
     */
    public List<String> toStringList(List<WordExampleModel> wordExampleModelList){
        if(wordExampleModelList == null) return  List.of();
        return wordExampleModelList.
                stream().map(WordExampleModel::getExample)
                .collect(Collectors.toList());
    }

    /**
     * Genera un WordExampleModel desde un WordExampleDTO simple
     * @param wordExampleDTO
     * @return
     */
    public WordExampleModel toEntity(WordExampleDTO wordExampleDTO){
        return WordExampleModel.builder()
                .example(wordExampleDTO.getExample())
                .build();
    }
}
