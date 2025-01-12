package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewDTO;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private static final String INSERT_QUERY = """
            INSERT INTO reviews (
            	content,
            	is_positive,
            	user_id,
            	film_id)
            VALUES (?, ?, ?, ?)
            """;
    private static final String FIND_ONE_BY_ID_QUERY = """
            SELECT
            	r.ID,
            	r.CONTENT,
            	r.IS_POSITIVE,
            	r.USER_ID,
            	r.FILM_ID,
            	fr.RATING AS useful
            FROM
            	REVIEWS r
            JOIN FILMS_RATING fr ON
            	r.FILM_ID = fr.FILM_ID
            WHERE r.ID = ?
            """;
    private static final String FIND_ONE_BY_USER_ID_AND_FILM_ID_QUERY = """
            SELECT
            	r.ID,
            	r.CONTENT,
            	r.IS_POSITIVE,
            	r.USER_ID,
            	r.FILM_ID,
            	fr.RATING AS useful
            FROM
            	REVIEWS r
            JOIN FILMS_RATING fr ON
            	r.FILM_ID = fr.FILM_ID
            WHERE r.USER_ID = ? AND r.FILM_ID = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE
            	reviews
            SET
            	content = ?,
            	is_positive = ?,
            	user_id = ?,
            	film_id = ?
            WHERE
            	id = ?
            """;
    private static final String DELETE_QUERY = """
            DELETE
            FROM
            	reviews
            WHERE
            	id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT
            	r.ID,
            	r.CONTENT,
            	r.IS_POSITIVE,
            	r.USER_ID,
            	r.FILM_ID,
            	fr.RATING AS useful
            FROM
            	REVIEWS r
            JOIN FILMS_RATING fr ON
            	r.FILM_ID = fr.FILM_ID
            ORDER BY
            	fr.RATING DESC
            LIMIT ?
            """;
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
            SELECT
            	r.ID,
            	r.CONTENT,
            	r.IS_POSITIVE,
            	r.USER_ID,
            	r.FILM_ID,
            	fr.RATING AS useful
            FROM
            	REVIEWS r
            JOIN FILMS_RATING fr ON
            	r.FILM_ID = fr.FILM_ID
            WHERE r.FILM_ID = ?
            ORDER BY
            	fr.RATING DESC
            LIMIT ?
            """;
    private final JdbcTemplate jdbc;
    private final RowMapper<Review> reviewRowMapper;

    public Integer insert(CreateReviewDTO createReviewDTO) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, createReviewDTO.getContent());
            ps.setObject(2, createReviewDTO.getIsPositive());
            ps.setObject(3, createReviewDTO.getUserId());
            ps.setObject(4, createReviewDTO.getFilmId());
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            log.error("Произошла ошибка при сохранении отзыва.");
            throw new InternalServerException("Произошла ошибка при сохранении отзыва.");
        }
    }

    private Optional<Review> findOne(String query, Object... params) {
        try {
            Review result = jdbc.queryForObject(query, reviewRowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Optional<Review> findOneById(Integer id) {
        return findOne(FIND_ONE_BY_ID_QUERY, id);
    }

    public Optional<Review> findOneByUserIdAndFilmId(Integer userId, Integer filmId) {
        return findOne(FIND_ONE_BY_USER_ID_AND_FILM_ID_QUERY, userId, filmId);
    }

    public void update(Review review) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getId());
        if (rowsUpdated == 0) {
            log.error("Произошла ошибка при обновлении отзыва.");
            throw new InternalServerException("Произошла ошибка при обновлении отзыва.");
        }
    }

    public boolean delete(Integer id) {
        int rowsDeleted = jdbc.update(DELETE_QUERY, id);
        return rowsDeleted > 0;
    }

    public List<Review> findAll(Integer count) {
        return jdbc.query(FIND_ALL_QUERY, reviewRowMapper, count);
    }

    public List<Review> findAllByFilmId(Integer filmId, Integer count) {
        return jdbc.query(FIND_ALL_BY_FILM_ID_QUERY, reviewRowMapper, filmId, count);
    }
}
