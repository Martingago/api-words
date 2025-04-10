package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.*;
import com.martingago.words.domain.repository.WordQualificationRepository;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordSingleProcessService {

    private final WordQualificationRepository qualificationRepository;
    private final CreateWordModelService createWordModelService;

    public WordModel processSingle(WordBatchDTO dto, Map<String, LanguageModel> languageMap) {
        LanguageModel lang = languageMap.get(dto.getLanguage());
        if (lang == null) throw new IllegalArgumentException("Idioma no válido");

        // Se crea el WordModel con el idioma indicado.
        WordModel word = createWordModelService.createWord(dto, lang);

        //Crea el Set de definiciones de la palabra.
        Set<WordDefinitionModel> definitions = dto.getDefinitions().stream().map(defDto -> {
            //Busca las qualificaciones de forma individual (Deberia optimizarse a una única consulta)
            WordQualificationModel qual = qualificationRepository
                    .findByQualification(defDto.getQualification())
                    .orElseGet(() -> qualificationRepository.save(
                            WordQualificationModel.builder()
                                    .qualification(defDto.getQualification())
                                    .build()
                    ));

            WordDefinitionModel def = createWordModelService.createDefinition(defDto, word, qual);
            def.setWordExampleModelSet(createWordModelService.createExamples(defDto, def));
            def.setWordRelationModelSet(createWordModelService.buildRelations(
                    defDto.getSynonyms(), def, RelationEnumType.SINONIMA, name -> {
                        WordModel rel = new WordModel();
                        rel.setWord(name);
                        rel.setLength(name.length());
                        rel.setLanguageModel(lang);
                        rel.setPlaceholder(true);
                        return rel;
                    }));

            return def;
        }).collect(Collectors.toSet());

        word.setWordDefinitionModelSet(definitions);
        return word;
    }
}

