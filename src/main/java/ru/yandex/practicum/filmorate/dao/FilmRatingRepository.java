package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmRatingRepository {
    private static final String INSERT_QUERY = """
            INSERT INTO films_rating (
            	film_id,
            	rating)
            VALUES (?, ?)
            """;
    private static final String FIND_ONE_BY_FILM_ID_QUERY = """
            SELECT
            	*
            FROM
            	films_rating
            WHERE
            	film_id = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE
            	films_rating
            SET
            	rating = ?
            WHERE
            	film_id = ?
            """;
    private static final String DELETE_QUERY = """
            DELETE
            FROM
            	films_rating
            WHERE
            	film_id = ?
            """;
    private final JdbcTemplate jdbc;
    private final RowMapper<FilmRating> filmRatingRowMapper;

    public Integer insert(Integer filmId, Integer rating) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, filmId);
            ps.setObject(2, rating);
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            log.error("Произошла ошибка при сохранении рейтинга фильма.");
            throw new InternalServerException("Произошла ошибка при сохранении рейтинга фильма.");
        }
    }

    private Optional<FilmRating> findOne(Object... params) {
        try {
            FilmRating result = jdbc.queryForObject(FilmRatingRepository.FIND_ONE_BY_FILM_ID_QUERY, filmRatingRowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Optional<FilmRating> findOneByFilmId(Integer filmId) {
        return findOne(filmId);
    }

    public void update(Integer filmId, Integer rating) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY, rating, filmId);
        if (rowsUpdated == 0) {
            log.error("Произошла ошибка при обновлении отзыва.");
            throw new InternalServerException("Произошла ошибка при обновлении отзыва.");
        }
    }

    public boolean delete(Integer filmId) {
        int rowsDeleted = jdbc.update(DELETE_QUERY, filmId);
        return rowsDeleted > 0;
    }
}
