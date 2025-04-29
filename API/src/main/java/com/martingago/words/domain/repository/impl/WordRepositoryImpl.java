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

    /**
     * Realiza una consulta de palabras con paginacion a la base de datos sin sobrecargar la consulta.
     * Esta función devolverá únicamente objetos WordModel sin las relaciones cargadas.
     * @param startsWith cadena por la que empieza la palabra a filtrar
     * @param endsWith cadena por la que termina la palabra a filtrar
     * @param length tamaño que debe tener la palabra a buscar
     * @param langCode código de idioma de la palabra a buscar
     * @param qualifications listado de clasificaciones que debe contener una palabra
     * @param pageable objeto de paginación que contiene información sobre la página y número de elementos a mostrar por página
     * @return Page con objetos WordModel con la información correspondiente filtrada por los parámetros del usuario
     */
    @Override
    public Page<WordModel> getWordsWithPagination(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {

        String baseQuery = "FROM WordModel w JOIN w.languageModel lang WHERE w.isPlaceholder = false";
        String countQuery = "SELECT COUNT(w) " + baseQuery;
        String selectQuery = "SELECT w " + baseQuery;

        StringBuilder filters = new StringBuilder();
        if (startsWith != null) filters.append(" AND w.word LIKE :startsWith");
        if (endsWith != null) filters.append(" AND w.word LIKE :endsWith");
        if (length != null) filters.append(" AND LENGTH(w.word) = :length");
        if (langCode != null) filters.append(" AND lang.langCode = :langCode");
        if (qualifications != null && !qualifications.isEmpty()) {
            filters.append(" AND EXISTS (SELECT 1 FROM WordDefinitionModel wd JOIN wd.wordQualificationModel wq WHERE wd.word = w AND wq.qualification IN :qualifications)");
        }

        TypedQuery<WordModel> query = entityManager.createQuery(selectQuery + filters + " ORDER BY w.word ASC", WordModel.class);
        TypedQuery<Long> count = entityManager.createQuery(countQuery + filters, Long.class);

        applyParameters(query, startsWith, endsWith, length, langCode, qualifications);
        applyParameters(count, startsWith, endsWith, length, langCode, qualifications);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<WordModel> results = query.getResultList();
        long total = count.getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }


    /**
     * Consulta paginada eficiente de palabras con filtros y carga posterior de relaciones.
     * @param startsWith cadena por la que empieza la palabra a filtrar
     * @param endsWith cadena por la que termina la palabra a filtrar
     * @param length tamaño que debe tener la palabra a buscar
     * @param langCode código de idioma de la palabra a buscar
     * @param qualifications listado de clasificaciones que debe contener una palabra
     * @param pageable objeto de paginación que contiene información sobre la página y número de elementos a mostrar por página
     * @return Page con objetos WordModel con la información correspondiente filtrada por los parámetros del usuario
     */
    @Override
    public Page<WordModel> getComplexWordsWithPagination(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {

        String baseQuery = "FROM WordModel w JOIN w.languageModel lang WHERE w.isPlaceholder = false";
        String countQuery = "SELECT COUNT(w) " + baseQuery;
        String selectQuery = "SELECT w " + baseQuery;

        StringBuilder filters = new StringBuilder();
        if (startsWith != null) filters.append(" AND w.word LIKE :startsWith");
        if (endsWith != null) filters.append(" AND w.word LIKE :endsWith");
        if (length != null) filters.append(" AND LENGTH(w.word) = :length");
        if (langCode != null) filters.append(" AND lang.langCode = :langCode");
        if (qualifications != null && !qualifications.isEmpty()) {
            filters.append(" AND EXISTS (SELECT 1 FROM WordDefinitionModel wd JOIN wd.wordQualificationModel wq WHERE wd.word = w AND wq.qualification IN :qualifications)");
        }

        TypedQuery<WordModel> query = entityManager.createQuery(selectQuery + filters + " ORDER BY w.word ASC", WordModel.class);
        TypedQuery<Long> count = entityManager.createQuery(countQuery + filters, Long.class);

        applyParameters(query, startsWith, endsWith, length, langCode, qualifications);
        applyParameters(count, startsWith, endsWith, length, langCode, qualifications);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<WordModel> words = query.getResultList();
        long total = count.getSingleResult();

        // Carga secundaria de relaciones solo si hay resultados
        if (!words.isEmpty()) {
            List<Long> wordIds = words.stream().map(WordModel::getId).toList();
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

    /**
     * Función que se encarga de aplicar los filtros a una consutla SQL
     * @param query query realizada por el usuario
     * @param startsWith cadena por la que empieza la palabra a filtrar
     * @param endsWith cadena por la que termina la palabra a filtrar
     * @param length tamaño que debe tener la palabra a buscar
     * @param langCode código de idioma de la palabra a buscar
     * @param qualifications listado de clasificaciones que debe contener una palabra
     */
    private void applyParameters(TypedQuery<?> query, String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications) {
        if (startsWith != null) query.setParameter("startsWith", startsWith + "%");
        if (endsWith != null) query.setParameter("endsWith", "%" + endsWith);
        if (length != null) query.setParameter("length", length);
        if (langCode != null) query.setParameter("langCode", langCode);
        if (qualifications != null && !qualifications.isEmpty()) query.setParameter("qualifications", qualifications);
    }

}

