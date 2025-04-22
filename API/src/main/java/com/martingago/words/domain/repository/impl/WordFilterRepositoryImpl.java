package com.martingago.words.domain.repository.impl;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.custom.WordFilterRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class WordFilterRepositoryImpl implements WordFilterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WordModel> findWordsWithFilters(String startsWith, String endsWith, Integer length, String langCode) {
        StringBuilder queryStr = new StringBuilder("SELECT w FROM WordModel w WHERE w.isPlaceholder = false ");

        if (startsWith != null) {
            queryStr.append("AND w.word LIKE :startsWith ");
        }
        if (endsWith != null) {
            queryStr.append("AND w.word LIKE :endsWith ");
        }
        if (length != null) {
            queryStr.append("AND LENGTH(w.word) = :length ");
        }
        if (langCode != null) {
            queryStr.append("AND w.languageModel.langCode = :langCode ");
        }

        TypedQuery<WordModel> query = entityManager.createQuery(queryStr.toString(), WordModel.class);

        if (startsWith != null) {
            query.setParameter("startsWith", startsWith + "%");
        }
        if (endsWith != null) {
            query.setParameter("endsWith", "%" + endsWith);
        }
        if (length != null) {
            query.setParameter("length", length);
        }
        if (langCode != null) {
            query.setParameter("langCode", langCode);
        }

        return query.getResultList();
    }
}

