package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);

    List<Film> getRecommendedFilms(Long id);
}