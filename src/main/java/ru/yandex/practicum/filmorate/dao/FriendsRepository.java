package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class FriendsRepository implements FriendsStorage {
    private static final String INSERT_QUERY = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT COUNT(*) FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT COUNT(*) FROM friends WHERE" +
            " (user_id = ? AND friend_id = ?) AND (friend_id = ? AND user_id = ?)";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT u.* FROM users AS u" +
            " JOIN friends AS f ON u.id = f.friend_id WHERE f.user_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users AS u" +
            " JOIN friends AS f1 ON u.id = f1.friend_id JOIN friends AS f2 ON u.id = f2.friend_id" +
            " WHERE f1.user_id = ? AND f2.user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Long id, Long friendId) {
        checkUser(id);
        checkUser(friendId);
        try {

            Integer count = jdbc.queryForObject(FIND_FRIENDS_QUERY, Integer.class, id, friendId, id, friendId);
            if (count > 0) {
                log.error("Пользователи уже друзья");
                throw new AlreadyFriendsException("Пользователи уже являются друзьями");
            }

            jdbc.update(INSERT_QUERY, id, friendId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении друга");
            throw new InternalServerException("Ошибка при добавлении друга");
        }
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        checkUser(id);
        checkUser(friendId);
        try {
            jdbc.update(DELETE_QUERY, id, friendId);
        } catch (Exception e) {
            log.error("Ошибка при удалении друга");
            throw new InternalServerException("Ошибка при удалении друга");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        checkUser(id);
        try {
            return jdbc.query(FIND_USER_FRIENDS_QUERY, UserMapper::mapToUser, id);
        } catch (Exception e) {
            log.error("Ошибка при поиске друзей пользователя");
            throw new InternalServerException("Ошибка при поиске друзей пользователя");
        }
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        checkUser(id);
        checkUser(otherId);
        try {
            return jdbc.query(FIND_COMMON_FRIENDS_QUERY, UserMapper::mapToUser, id, otherId);
        } catch (Exception e) {
            log.error("Ошибка при поиске общих друзей пользователей");
            throw new InternalServerException("Ошибка при поиске общих друзей пользователей");
        }
    }

    private void checkUser(Long id) {
        Integer idCount = jdbc.queryForObject(FIND_BY_ID_QUERY, Integer.class, id);
        if (idCount == 0) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException("Пользователь с данным id не найден");
        }
    }
}