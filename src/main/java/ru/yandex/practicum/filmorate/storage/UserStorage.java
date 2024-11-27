package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

    Map<Long, User> getUsers();
}