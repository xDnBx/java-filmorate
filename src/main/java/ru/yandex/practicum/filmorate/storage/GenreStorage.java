package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;

public interface GenreStorage {
    Collection<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    void checkGenres(LinkedHashSet<Genre> genres);
}
