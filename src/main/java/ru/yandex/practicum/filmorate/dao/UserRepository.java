package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dao.queries.UserQueries;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище пользователей.
 */
@Repository
@Slf4j
public class UserRepository extends BaseRepository implements UserStorage {

    //region Пользователи

    /**
     * Создать нового пользователя.
     *
     * @param user пользователь.
     * @return пользователь.
     */
    @Override
    public User createUser(User user) {
        try {
            long id = this.insert(UserQueries.CREATE_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            user.setId(id);

            log.debug("Пользователь {} успешно добавлен с id = {}", user.getLogin(), user.getId());
            return user;
        } catch (DuplicateKeyException ex) {
            log.error("Ошибка при добавлении пользователя {}: [DuplicateKeyException] {}", user.getLogin(), ex.getMessage());
            throw new DuplicatedDataException(String.format("Пользователь с таким логином (%s) или email (%s) уже существует", user.getLogin(), user.getEmail()));
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении пользователя {}: [{}] {}", user.getLogin(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей.
     */
    @Override
    public Collection<User> getAllUsers() {
        try {
            return this.findMany(UserQueries.GET_ALL_USERS_QUERY, UserMapper::mapToUser);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка пользователей: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    @Override
    public Optional<User> getUserById(long userId) {
        try {
            return this.findOne(UserQueries.GET_USER_BY_ID_QUERY, UserMapper::mapToUser, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении пользователя с id = {}: [{}] {}", userId, ex.getClass().getSimpleName(), ex.toString());
            throw new InternalServerException();
        }
    }

    /**
     * Обновить пользователя.
     *
     * @param user пользователь.
     */
    @Override
    public void updateUser(User user) {
        this.getUserById(user.getId());

        try {
            this.update(UserQueries.UPDATE_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            log.debug("Пользователь с id = {} успешно обновлен", user.getId());
        } catch (Throwable ex) {
            log.error("Ошибка при обновлении пользователя с id = {}: {}", user.getId(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    @Override
    public void deleteUser(long userId) {
        try {
            this.delete(UserQueries.DELETE_USER_QUERY, userId);
            log.debug("Пользователь с id = {} успешно удален", userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя");
            throw new InternalServerException("Ошибка при удалении пользователя");
        }
    }

    //endregion

    //region Друзья

    /**
     * Добавить пользователя в друзья.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    @Override
    public void addFriend(long userId, long friendId) {
        try {
            this.insert(UserQueries.ADD_FRIEND_TO_USER_QUERY, userId, friendId);
            log.debug("Пользователь с id = {} успешно добавлен в друзья пользователю с id = {}", friendId, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении друга с id = {} для пользователя с id = {}: [{}] {}", friendId, userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список друзей пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список пользователей.
     */
    @Override
    public Collection<User> getFriends(long userId) {
        try {
            return this.findMany(UserQueries.GET_USER_FRIENDS_QUERY, UserMapper::mapToUser, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении друзей пользователя с id = {}: [{}] {}", userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список общих друзей двух пользователей.
     *
     * @param firstUserId  идентификатор первого пользователя.
     * @param secondUserId идентификатор второго пользователя.
     * @return список пользователей.
     */
    @Override
    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) {
        try {
            return this.findMany(UserQueries.GET_COMMON_FRIENDS_QUERY, UserMapper::mapToUser, firstUserId, secondUserId);
        } catch (Throwable ex) {
            log.error("Ошибка при поиске общих друзей пользователей с идентификаторами {} и {}: [{}] {}", firstUserId, secondUserId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException("Ошибка при поиске общих друзей пользователей");
        }
    }

    /**
     * Удалить пользователя из друзей.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    @Override
    public void deleteFriend(long userId, long friendId) {
        try {
            this.delete(UserQueries.DELETE_USER_FROM_FRIENDS_QUERY, userId, friendId);
            log.debug("Пользователь с id = {} успешно удален из друзей у пользователя с id = {}", friendId, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении друга с id = {} для пользователя с id = {}: [{}] {}", friendId, userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    //endregion
}