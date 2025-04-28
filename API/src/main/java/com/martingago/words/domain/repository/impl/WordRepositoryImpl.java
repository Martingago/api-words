package com.martingago.words.domain.repository.impl;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.custom.WordFilterRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Realiza una consulta de palabras con paginacion a la base de datos sin sobrecargar la consulta.
     * @param startsWith
     * @param endsWith
     * @param length
     * @param langCode
     * @param qualifications
     * @param pageable
     * @return
     */
    @Override
    public Page<WordModel> getWordsWithPagination(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {
        StringBuilder baseQuery = new StringBuilder("FROM WordModel " +
                "w JOIN w.languageModel lang " +
                "LEFT JOIN w.wordDefinitionModelSet wd " +
                "LEFT JOIN wd.wordQualificationModel wq " +
                "WHERE w.isPlaceholder = false ");
        StringBuilder filterQuery = new StringBuilder();

        if (startsWith != null) {
            filterQuery.append("AND w.word LIKE :startsWith ");
        }
        if (endsWith != null) {
            filterQuery.append("AND w.word LIKE :endsWith ");
        }
        if (length != null) {
            filterQuery.append("AND LENGTH(w.word) = :length ");
        }
        if (langCode != null) {
            filterQuery.append("AND lang.langCode = :langCode ");
        }
        if (qualifications != null && !qualifications.isEmpty()) {
            filterQuery.append("AND wq.qualification IN :qualifications ");
        }

        // Consulta principal paginada
        String fullQueryStr = "SELECT DISTINCT w " + baseQuery + filterQuery + "ORDER BY w.word ASC";
        TypedQuery<WordModel> query = entityManager.createQuery(fullQueryStr, WordModel.class);

        // Consulta de conteo
        String countQueryStr = "SELECT COUNT(DISTINCT w) " + baseQuery + filterQuery;
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryStr, Long.class);

        // Parámetros compartidos
        if (startsWith != null) {
            query.setParameter("startsWith", startsWith + "%");
            countQuery.setParameter("startsWith", startsWith + "%");
        }
        if (endsWith != null) {
            query.setParameter("endsWith", "%" + endsWith);
            countQuery.setParameter("endsWith", "%" + endsWith);
        }
        if (length != null) {
            query.setParameter("length", length);
            countQuery.setParameter("length", length);
        }
        if (langCode != null) {
            query.setParameter("langCode", langCode);
            countQuery.setParameter("langCode", langCode);
        }
        if (qualifications != null && !qualifications.isEmpty()) {
            query.setParameter("qualifications", qualifications);
            countQuery.setParameter("qualifications", qualifications);
        }

        // Paginación real
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<WordModel> results = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }


    @Override
    public Page<WordModel> getComplexWordsWithPagination(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {

        // --- Consulta principal SOLO WordModel, sin FETCH ---
        StringBuilder baseQuery = new StringBuilder("FROM WordModel w JOIN w.languageModel lang WHERE w.isPlaceholder = false ");
        StringBuilder filterQuery = new StringBuilder();

        if (startsWith != null) {
            filterQuery.append("AND w.word LIKE :startsWith ");
        }
        if (endsWith != null) {
            filterQuery.append("AND w.word LIKE :endsWith ");
        }
        if (length != null) {
            filterQuery.append("AND LENGTH(w.word) = :length ");
        }
        if (langCode != null) {
            filterQuery.append("AND lang.langCode = :langCode ");
        }
        if (qualifications != null && !qualifications.isEmpty()) {
            filterQuery.append("AND EXISTS (SELECT 1 FROM WordDefinitionModel wd JOIN wd.wordQualificationModel wq WHERE wd.wordModel = w AND wq.qualification IN :qualifications) ");
        }

        // Consulta paginada principal
        String fullQueryStr = "SELECT w " + baseQuery + filterQuery + "ORDER BY w.word ASC";
        TypedQuery<WordModel> query = entityManager.createQuery(fullQueryStr, WordModel.class);

        // Consulta para contar total
        String countQueryStr = "SELECT COUNT(w) " + baseQuery + filterQuery;
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryStr, Long.class);

        // Parámetros
        if (startsWith != null) {
            query.setParameter("startsWith", startsWith + "%");
            countQuery.setParameter("startsWith", startsWith + "%");
        }
        if (endsWith != null) {
            query.setParameter("endsWith", "%" + endsWith);
            countQuery.setParameter("endsWith", "%" + endsWith);
        }
        if (length != null) {
            query.setParameter("length", length);
            countQuery.setParameter("length", length);
        }
        if (langCode != null) {
            query.setParameter("langCode", langCode);
            countQuery.setParameter("langCode", langCode);
        }
        if (qualifications != null && !qualifications.isEmpty()) {
            query.setParameter("qualifications", qualifications);
            countQuery.setParameter("qualifications", qualifications);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<WordModel> words = query.getResultList();
        Long total = countQuery.getSingleResult();

        // --- Opcional: cargar relaciones extra solo para resultados paginados ---
        if (!words.isEmpty()) {
            List<Long> wordIds = words.stream().map(WordModel::getId).toList();

            // Cargar definiciones, ejemplos, relaciones, etc en batch
            entityManager.createQuery(
                            "SELECT DISTINCT w FROM WordModel w " +
                                    "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
                                    "LEFT JOIN FETCH wd.wordQualificationModel wq " +
                                    "LEFT JOIN FETCH wd.wordExampleModelSet we " +
                                    "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
                                    "LEFT JOIN FETCH wr.wordRelated wrd " +
                                    "WHERE w.id IN :ids", WordModel.class)
                    .setParameter("ids", wordIds)
                    .getResultList();
        }

        return new org.springframework.data.domain.PageImpl<>(words, pageable, total);
    }

}

