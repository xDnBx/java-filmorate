package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreRepository implements GenreStorage {

    private static final String GET_ALL_QUERY = "SELECT * FROM genres";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    private static final String FIND_BY_ID_FILM_QUERY = "SELECT distinct g.id, g.NAME FROM genres g , FILMS_GENRES fg WHERE fg.FILM_ID = ? and g.id=fg.GENRE_ID";

    private final JdbcTemplate jdbc;

    @Override
    public Collection<Genre> getAllGenres() {
        try {
            return jdbc.query(GET_ALL_QUERY, GenreMapper::mapToGenre);
        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров");
            throw new InternalServerException("Ошибка при получении списка жанров");
        }
    }

    @Override
    public Genre getGenreById(Integer id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, GenreMapper::mapToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске жанра");
            throw new NotFoundException("Жанр с данным id не найден");
        }
    }

    @Override
    public void checkGenres(LinkedHashSet<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        try {
            for (Genre genre : genres) {
                jdbc.queryForObject(FIND_BY_ID_QUERY, GenreMapper::mapToGenre, genre.getId());
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске жанра");
            throw new ValidationException("Жанр с данным id не найден");
        }
    }

    @Override
    public LinkedHashSet<Genre> getGenreByFilmId(Long filmId) {
        try {
            List<Genre> list = jdbc.query(FIND_BY_ID_FILM_QUERY, GenreMapper::mapToGenre, filmId);
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(list);
            log.info("Получено {} жанров для фильма с id = {}", genres.size(), filmId);
            return genres;
        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров");
            return new LinkedHashSet<>();
        }
    }
}