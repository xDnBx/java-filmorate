package ru.yandex.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private static final String FIND_ONE_BY_FILM_ID_AND_USER_ID_QUERY = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
    private final JdbcTemplate jdbc;
    private final RowMapper<Like> likeRowMapper;

    public Optional<Like> findOneByFilmIdAndUserId(Integer filmId, Integer userId) {
        try {
            Like result = jdbc.queryForObject(FIND_ONE_BY_FILM_ID_AND_USER_ID_QUERY, likeRowMapper, filmId, userId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}