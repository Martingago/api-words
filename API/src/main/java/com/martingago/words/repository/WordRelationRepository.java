package com.martingago.words.repository;

import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordRelationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordRelationRepository extends JpaRepository<WordRelationModel, Long> {

    @Query("""
    SELECT DISTINCT wrm.wordRelated.word 
    FROM WordRelationModel wrm 
    JOIN wrm.wordDefinitionModel wdm 
    JOIN wdm.word wm
    JOIN wm.languageModel lm
    WHERE wm.word = :word 
    AND lm.langCode = :langCode
    AND wrm.relationEnumType = :relationType
    """)
    List<String> findSynonymsByWord(@Param("word") String word,
                                    @Param("relationType")RelationEnumType relationType,
                                    @Param("langCode")String langCode);

}
