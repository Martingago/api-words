package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.models.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FilterWordsService {

    private final WordRepository wordRepository;

    /**
     * Devuelve una página con los objetos wordModel encontrados en la solicitud
     * @param startsWith Filtrar palabras que comiencen con este prefijo (opcional)
     * @param endsWith Filtrar palabras que terminen con este sufijo (opcional)
     * @param length Filtrar por longitud exacta (opcional)
     * @param langCode Filtrar por código de idioma (opcional)
     * @param qualifications Filtrar por la qualificacion de la palabra.
     * @param pageable objeto de paginación
     * @return Page con los WordModel encontrados en la base de datos.
     */
    public Page<WordModel> getPaginatedFilteredWords(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {
        return wordRepository.getWordsWithPagination(startsWith, endsWith, length, langCode, qualifications, pageable);
    }

    /**
     * Devuelve una página con los objetos wordModel encontrados en la solicitud
     * @param startsWith Filtrar palabras que comiencen con este prefijo (opcional)
     * @param endsWith Filtrar palabras que terminen con este sufijo (opcional)
     * @param length Filtrar por longitud exacta (opcional)
     * @param langCode Filtrar por código de idioma (opcional)
     * @param qualifications Filtrar por la qualificacion de la palabra.
     * @param pageable objeto de paginación
     * @return Page con los WordModel encontrados en la base de datos.
     */
    public Page<WordModel> getPaginatedFilteredComplexWords(String startsWith, String endsWith, Integer length, String langCode, List<String> qualifications, Pageable pageable) {
        return wordRepository.getComplexWordsWithPagination(startsWith, endsWith, length, langCode, qualifications, pageable);
    }
}
