package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища пользователей.
 */
public interface UserStorage {

    //region Пользователи

    /**
     * Создать нового пользователя.
     *
     * @param user пользователь.
     * @return пользователь.
     */
    User createUser(User user);

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей.
     */
    Collection<User> getAllUsers();

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    Optional<User> getUserById(long userId);

    /**
     * Обновить пользователя.
     *
     * @param user пользователь.
     */
    void updateUser(User user);

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    void deleteUser(long userId);

    //endregion

    //region Друзья

    /**
     * Добавить пользователя в друзья.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    void addFriend(long userId, long friendId);

    /**
     * Получить список друзей пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список пользователей.
     */
    Collection<User> getFriends(long userId);

    /**
     * Получить список общих друзей двух пользователей.
     *
     * @param firstUserId  идентификатор первого пользователя.
     * @param secondUserId идентификатор второго пользователя.
     * @return список пользователей.
     */
    Collection<User> getCommonFriends(long firstUserId, long secondUserId);

    /**
     * Удалить пользователя из друзей.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    void deleteFriend(long userId, long friendId);

    //endregion
}