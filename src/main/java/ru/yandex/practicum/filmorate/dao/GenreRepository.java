package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.dao.queries.GenreQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище жанров.
 */
@Repository
@Slf4j
public class GenreRepository extends BaseRepository implements GenreStorage {
    /**
     * Получить список всех жанров.
     *
     * @return список жанров.
     */
    @Override
    public Collection<Genre> getAllGenres() {
        try {
            return this.findMany(GenreQueries.GET_ALL_GENRES_QUERY, GenreMapper::mapToGenre);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка жанров: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить жанр по его идентификатору.
     *
     * @param genreId идентификатор жанра.
     * @return жанр.
     */
    @Override
    public Optional<Genre> getGenreById(long genreId) {
        try {
            return this.findOne(GenreQueries.GET_GENRE_BY_ID_QUERY, GenreMapper::mapToGenre, genreId);
        } catch (Throwable ex) {
            log.error("Ошибка при поиске жанра с id = {}: [{}] {}", genreId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }
}