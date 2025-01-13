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
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserRepository implements UserStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM users";
    private static final String GET_RECOMMENDED_FILMS = """
            SELECT f.*,m.id AS mpa_id,m.name AS mpa_name
            FROM films AS f
            JOIN likes AS l3 ON f.id = l3.film_id
            JOIN mpa AS m
            WHERE l3.film_id not in (SELECT l.film_id
                                     FROM likes AS l
                                     WHERE l.user_id = ?)
            AND l3.user_id = (SELECT u.user_id
                              FROM (select l2.user_id, count(l2.film_id) AS c
                                    FROM likes l2
                                    WHERE l2.film_id IN (SELECT l.film_id
                                                         FROM likes as l
                                                         WHERE l.user_id = ?)
                                                         AND l2.user_id != ?
                                    GROUP BY l2.user_id
                                    ORDER BY c
                                    LIMIT 1) AS u)
            """;
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

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
    public List<Film> getRecommendedFilms(Long id) {
        List<Film> films = jdbc.queryForStream(GET_RECOMMENDED_FILMS, FilmMapper::mapToFilm, id, id, id).collect(Collectors.toSet()).stream().toList();
        System.out.println(films);
        return films;
    }
}