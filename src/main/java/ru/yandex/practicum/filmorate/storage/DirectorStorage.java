package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director createDirector(Director director);

    Collection<Director> getAllDirectors();

    Director getDirectorById(Integer directorId);

    Director updateDirector(Director newDirector);

    void deleteDirector(Integer directorId);
}
