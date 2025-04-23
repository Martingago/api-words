package com.martingago.words.domain.repository.impl;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.custom.WordFilterRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WordRepositoryImpl implements WordFilterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WordModel> getWordsWithFilters(String startsWith, String endsWith, Integer length, String langCode) {

        StringBuilder queryStr = new StringBuilder("SELECT w FROM WordModel w JOIN FETCH w.languageModel ");
        boolean hasConditions = false;

        if (startsWith != null || endsWith != null || length != null || langCode != null) {
            queryStr.append("WHERE ");
        }

        if (startsWith != null) {
            queryStr.append("w.word LIKE :startsWith ");
            hasConditions = true;
        }
        if (endsWith != null) {
            queryStr.append(hasConditions ? "AND " : "").append("w.word LIKE :endsWith ");
            hasConditions = true;
        }
        if (length != null) {
            queryStr.append(hasConditions ? "AND " : "").append("LENGTH(w.word) = :length ");
            hasConditions = true;
        }
        if (langCode != null) {
            queryStr.append(hasConditions ? "AND " : "").append("w.languageModel.langCode = :langCode ");
        }

        queryStr.append("ORDER BY w.word ASC");

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

    @Override
    public List<WordModel> getWordsWithExtendFilters(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT w FROM WordModel w " +
                "JOIN FETCH w.languageModel " +
                "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
                "LEFT JOIN FETCH wd.wordQualificationModel wq " +
                "WHERE w.isPlaceholder = false ");

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
        if (qualifications != null && !qualifications.isEmpty()) {
            queryStr.append("AND wq.qualification IN (:qualificationList) ");
        }

        queryStr.append("ORDER BY w.word ASC");

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
        if (qualifications != null && !qualifications.isEmpty()) {
            query.setParameter("qualificationList", qualifications);
        }

        return query.getResultList();

    }
}

