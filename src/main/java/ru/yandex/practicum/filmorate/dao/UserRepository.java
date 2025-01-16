package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserRepository implements UserStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM users";
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
}