package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class UserService {
    /**
     * Хранилище пользователей.
     */
    private final UserStorage userStorage;

    private final EventRepository eventRepository;

    //region Пользователи

    /**
     * Создать нового пользователя.
     *
     * @param user пользователь.
     * @return пользователь.
     */
    public User createUser(User user) {
        log.debug("Добавление нового пользователя с логином {}", user.getLogin());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.createUser(user);
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей.
     */
    public Collection<User> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    public User getUserById(long userId) {
        log.debug("Получение пользователя с id = {}", userId);

        Optional<User> userOptional = this.userStorage.getUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return userOptional.get();
    }

    /**
     * Обновить пользователя.
     *
     * @param user пользователь.
     * @return пользователь.
     */
    public User updateUser(User user) {
        this.throwIfUsersNotFound(user.getId());
        log.debug("Обновление пользователя с id = {}", user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        this.userStorage.updateUser(user);
        return this.getUserById(user.getId());
    }

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    public void deleteUser(long userId) {
        this.throwIfUsersNotFound(userId);
        log.debug("Удаление пользователя с id = {}", userId);

        this.userStorage.deleteUser(userId);
    }

    //endregion

    // region Друзья

    /**
     * Добавить пользователя в друзья.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    public void addFriend(long userId, long friendId) {
        this.throwIfUsersNotFound(userId);
        this.throwIfUsersNotFound(friendId);
        log.debug("Добавление пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);

        this.userStorage.addFriend(userId, friendId);
        this.eventRepository.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    /**
     * Получить список друзей пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список пользователей.
     */
    public Collection<User> getFriends(long userId) {
        this.throwIfUsersNotFound(userId);
        log.debug("Получение списка друзей пользователя с id = {}", userId);

        return this.userStorage.getFriends(userId);
    }

    /**
     * Получить список общих друзей двух пользователей.
     *
     * @param firstUserId  идентификатор первого пользователя.
     * @param secondUserId идентификатор второго пользователя.
     * @return список пользователей.
     */
    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) {
        this.throwIfUsersNotFound(firstUserId);
        this.throwIfUsersNotFound(secondUserId);
        log.debug("Получение списка общих друзей пользователей с идентификаторами {} и {}", firstUserId, secondUserId);

        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }

    /**
     * Удалить пользователя из друзей.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    public void deleteFriend(long userId, long friendId) {
        this.throwIfUsersNotFound(userId);
        this.throwIfUsersNotFound(friendId);
        log.debug("Удаление пользователя с id = {} из друзей пользователя с id = {}", friendId, userId);

        this.userStorage.deleteFriend(userId, friendId);
        this.eventRepository.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    //endregion

    //region Facilities

    /**
     * Выбросить исключение, если пользователь не найден.
     *
     * @param userId идентификатор пользователя.
     */
    private void throwIfUsersNotFound(long userId) {
        if (this.userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //endregion
}