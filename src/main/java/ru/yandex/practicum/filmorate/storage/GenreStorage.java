package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища жанров.
 */
public interface GenreStorage {
    /**
     * Получить список всех жанров.
     *
     * @return список жанров.
     */
    Collection<Genre> getAllGenres();

    /**
     * Получить жанр по его идентификатору.
     *
     * @param genreId идентификатор жанра.
     * @return жанр.
     */
    Optional<Genre> getGenreById(long genreId);
}
