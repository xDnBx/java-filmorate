package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRatingRepository {
    private static final String INSERT_QUERY = """
            INSERT INTO REVIEWS_RATING (
            	REVIEW_ID,
            	RATING)
            VALUES (?, ?)
            """;
    private static final String FIND_ONE_BY_REVIEW_ID_QUERY = """
            SELECT
            	*
            FROM
            	REVIEWS_RATING
            WHERE
            	REVIEW_ID = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE
            	REVIEWS_RATING
            SET
            	RATING = ?
            WHERE
            	REVIEW_ID = ?
            """;
    private final JdbcTemplate jdbc;
    private final RowMapper<ReviewRating> reviewRatingRowMapper;

    public Integer insert(Integer reviewId, Integer rating) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, reviewId);
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

    private Optional<ReviewRating> findOne(Integer reviewId) {
        try {
            ReviewRating result = jdbc.queryForObject(ReviewRatingRepository.FIND_ONE_BY_REVIEW_ID_QUERY, reviewRatingRowMapper, reviewId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Optional<ReviewRating> findOneByReviewId(Integer reviewId) {
        return findOne(reviewId);
    }

    public void update(Integer reviewId, Integer rating) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY, rating, reviewId);
        if (rowsUpdated == 0) {
            log.error("Произошла ошибка при обновлении отзыва.");
            throw new InternalServerException("Произошла ошибка при обновлении отзыва.");
        }
    }
}
