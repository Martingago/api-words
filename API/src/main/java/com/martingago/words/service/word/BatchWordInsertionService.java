package com.martingago.words.service.word;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.repository.WordRepository;
import com.martingago.words.service.example.WordExampleService;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.qualification.WordQualificationService;
import com.martingago.words.service.relation.WordRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchWordInsertionService {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordDefinitionRepository wordDefinitionRepository;

    @Autowired
    private WordQualificationService wordQualificationService;


    /**
     * Recibe un Set de wordResponseDTO para añadir en la BBDD.
     * @param wordResponseDTOSet
     * @return
     */
    public Set<WordModel> insertBatchWordsSet(Set<WordResponseDTO> wordResponseDTOSet){
        //Obtener el listado de idiomas existente en la BBDD
        Map<String, LanguageModel> mappedLanguages = languageService.getAllLanguagesMappedByLangCode();

        List<WordModel> wordToInsertList = wordResponseDTOSet.stream()
                .filter(wordDto -> {
                    //Filtra por aquellas palabras cuyo idioma no exista en la BBDD.
                    if(!mappedLanguages.containsKey(wordDto.getLanguage())){
                        System.out.println("Skipping word" + wordDto.getWord() +  "because language " + wordDto.getLanguage()  + " does not exist in database");
                        return false;
                    }
                    return true;
                })
                .map(wordDto -> WordModel.builder()
                        .languageModel(mappedLanguages.get(wordDto.getLanguage()))
                        .wordLength(wordDto.getLength())
                        .word(wordDto.getWord())
                        .isPlaceholder(false)
                        .build()

                )
                .collect(Collectors.toList());

        return new HashSet<>(wordRepository.saveAll(wordToInsertList));
    }
}
