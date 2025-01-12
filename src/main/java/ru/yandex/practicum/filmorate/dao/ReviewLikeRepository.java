package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewLikeRepository {
    private static final String INSERT_QUERY = """
            INSERT INTO reviews_likes (
            	review_id,
            	user_id,
                is_liked)
            VALUES (?, ?, ?)
            """;
    private static final String FIND_ONE_BY_REVIEW_ID_AND_USER_ID_QUERY = """
            SELECT
            	*
            FROM
            	reviews_likes
            WHERE
            	review_id = ? AND user_id = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE
            	reviews_likes
            SET
            	is_liked = ?
            WHERE
            	review_id = ? AND user_id = ?
            """;
    private final JdbcTemplate jdbc;
    private final RowMapper<ReviewLike> reviewLikeRowMapper;

    public Integer insert(Integer reviewId, Integer userId, Boolean isLiked) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, reviewId);
            ps.setObject(2, userId);
            ps.setObject(3, isLiked);
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            log.error("Произошла ошибка при сохранении лайка отзыва фильма.");
            throw new InternalServerException("Произошла ошибка при сохранении лайка отзыва фильма.");
        }
    }

    private Optional<ReviewLike> findOne(Object... params) {
        try {
            ReviewLike result = jdbc.queryForObject(ReviewLikeRepository.FIND_ONE_BY_REVIEW_ID_AND_USER_ID_QUERY, reviewLikeRowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Optional<ReviewLike> findOneByReviewIdAndUserId(Integer reviewId, Integer userId) {
        return findOne(reviewId, userId);
    }

    public void update(Boolean isLiked, Integer reviewId, Integer userId) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY, isLiked, reviewId, userId);
        if (rowsUpdated == 0) {
            log.error("Произошла ошибка при обновлении лайка отзыва.");
            throw new InternalServerException("Произошла ошибка при обновлении лайка отзыва.");
        }
    }
}
