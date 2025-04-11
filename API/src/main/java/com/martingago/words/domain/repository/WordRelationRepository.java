package com.martingago.words.domain.repository;

import com.martingago.words.domain.model.RelationEnumType;
import com.martingago.words.domain.model.WordRelationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    List<String> findWordsRelatedByWord(@Param("word") String word,
                                        @Param("relationType")RelationEnumType relationType,
                                        @Param("langCode")String langCode);

}
