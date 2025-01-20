package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserRepository implements UserStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM users";
    private static final String GET_RECOMMENDED_FILMS = """
            SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
            FROM films AS f
            JOIN likes AS l3 ON f.id = l3.film_id
            JOIN mpa AS m ON f.mpa_id = m.id
            WHERE l3.film_id NOT IN (
                SELECT l.film_id
                FROM likes AS l
                WHERE l.user_id = ?
            )
            AND l3.user_id = (
                SELECT l2.user_id
                FROM likes l2
                WHERE l2.film_id IN (
                    SELECT l.film_id
                    FROM likes AS l
                    WHERE l.user_id = ?
                )
                AND l2.user_id != ?
                GROUP BY l2.user_id
                ORDER BY COUNT(l2.film_id) DESC
                LIMIT 1
            )
            """;
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_FRIENDS_QUERY = "DELETE FROM friends WHERE user_id = ? OR friend_id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE user_id = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE user_id = ?";
    private static final String DELETE_REVIEW_LIKE_QUERY = "DELETE FROM reviews_likes WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String FIND_GENRES_QUERY = "SELECT g.id, g.name FROM films_genres AS f" +
            " JOIN genres AS g ON f.genre_id = g.id WHERE f.film_id = ?";

    private final JdbcTemplate jdbc;

    @Override
    public Collection<User> getAllUsers() {
        try {
            return jdbc.query(GET_ALL_QUERY, UserMapper::mapToUser);
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей");
            throw new InternalServerException("Ошибка при получении списка пользователей");
        }
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String name = user.getName();
        if (name == null || name.isBlank() || name.isEmpty()) {
            user.setName(user.getLogin());
        }
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setString(4, user.getBirthday().toString());
                return ps;
            }, keyHolder);

            Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(id);
            log.info("Пользователь с id = {} успешно добавлен!", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя");
            throw new InternalServerException("Ошибка при добавлении пользователя");
        }
    }

    @Override
    public User updateUser(User newUser) {
        getUserById(newUser.getId());
        String name = newUser.getName();
        if (name == null || name.isBlank() || name.isEmpty()) {
            newUser.setName(newUser.getLogin());
        }
        try {
            jdbc.update(UPDATE_QUERY, newUser.getEmail(), newUser.getLogin(), newUser.getName(),
                    newUser.getBirthday().toString(), newUser.getId());
            log.info("Обновление пользователя с id = {} прошло успешно!", newUser.getId());
            return getUserById(newUser.getId());
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя");
            throw new InternalServerException("Ошибка при обновлении пользователя");
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, UserMapper::mapToUser, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске пользователя");
            throw new NotFoundException("Пользователь с данным id не найден");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            jdbc.update(DELETE_FRIENDS_QUERY, userId, userId);
            jdbc.update(DELETE_LIKE_QUERY, userId);
            jdbc.update(DELETE_REVIEW_QUERY, userId);
            jdbc.update(DELETE_REVIEW_LIKE_QUERY, userId);
            jdbc.update(DELETE_QUERY, userId);
            log.info("Удаление пользователя с id = {} прошло успешно!", userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя");
            throw new InternalServerException("Ошибка при удалении пользователя");
        }
    }

    @Override
    public List<Film> getRecommendedFilms(Long id) {
        getUserById(id);
        List<Film> films = jdbc.query(GET_RECOMMENDED_FILMS, FilmMapper::mapToFilm, id, id, id);
        films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
        return films;
    }

    private LinkedHashSet<Genre> getFilmGenres(Long id) {
        try {
            return new LinkedHashSet<>(Objects.requireNonNull(jdbc.query(FIND_GENRES_QUERY, GenreMapper::mapToGenre,
                    id)));
        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров");
            throw new InternalServerException("Ошибка при получении списка жанров");
        }
    }
}