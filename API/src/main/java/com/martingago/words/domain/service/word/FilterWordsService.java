package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.models.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FilterWordsService {

    private final WordRepository wordRepository;

    /**
     * Devuelve una lista de palabras aplicando filtros opcionales.
     * Si un parámetro es null, no se filtra por ese criterio.
     *
     * @param startsWith Filtrar palabras que comiencen con este prefijo (opcional)
     * @param endsWith Filtrar palabras que terminen con este sufijo (opcional)
     * @param length Filtrar por longitud exacta (opcional)
     * @param langCode Filtrar por código de idioma (opcional)
     * @return Lista de WordModel que cumplen los filtros.
     */
    public List<WordModel> getWordsFiltered(String startsWith,String endsWith, Integer length, String langCode) {
        return wordRepository.getWordsWithFilters(startsWith, endsWith, length, langCode);
    }

    /**
     * Devuelve una lista de palabras aplicando filtros opcionales.
     * Si un parámetro es null, no se filtra por ese criterio.
     *
     * @param startsWith Filtrar palabras que comiencen con este prefijo (opcional)
     * @param endsWith Filtrar palabras que terminen con este sufijo (opcional)
     * @param length Filtrar por longitud exacta (opcional)
     * @param langCode Filtrar por código de idioma (opcional)
     * @Param qualification Filtrar por la qualificacion de la palabra.
     * @return Lista de WordModel que cumplen los filtros.
     */
    public List<WordModel> getWordsExtendedFilters(String startsWith,String endsWith, Integer length, String langCode, List<String> qualifications){
        return  wordRepository.getWordsWithExtendFilters(startsWith, endsWith, length, langCode, qualifications);
    }
}
