package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища оценок Ассоциации кинокомпаний.
 */
public interface MpaStorage {
    /**
     * Получить список оценок Ассоциации кинокомпаний.
     *
     * @return список оценок Ассоциации кинокомпаний.
     */
    Collection<Mpa> getAllMpa();

    /**
     * Получить оценку Ассоциации кинокомпаний по её идентификатору.
     *
     * @param mpaId идентификатор оценки Ассоциации кинокомпаний.
     * @return оценка Ассоциации кинокомпаний.
     */
    Optional<Mpa> getMpaById(long mpaId);
}