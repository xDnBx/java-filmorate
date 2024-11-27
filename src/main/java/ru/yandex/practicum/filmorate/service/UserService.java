package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.info("Добавление нового пользователя: {}", user.getLogin());
        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        log.info("Обновление пользователя с id = {}", newUser.getId());
        return userStorage.updateUser(newUser);
    }

    public User getUserById(Long id) {
        log.info("Получение пользователя с id = {}", id);
        return userStorage.getUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Добавление пользователя с id = {} в друзья к пользователю с id = {}", friendId, id);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        log.info("Удаление пользователя с id = {} из друзей пользователя с id = {}", friendId, id);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(Long id) {
        log.info("Получение списка друзей пользователя с id = {}", id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Получение списка общих друзей пользователей с id = {} и с id = {}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }
}