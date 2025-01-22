package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с жанрами.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class GenreService {
    /**
     * Хранилище жанров.
     */
    private final GenreStorage genreStorage;

    /**
     * Получить список всех жанров.
     * @return список жанров.
     */
    public Collection<Genre> getAllGenres() {
        log.debug("Получение списка всех жанров");
        return this.genreStorage.getAllGenres();
    }

    /**
     * Получить жанр по его идентификатору.
     * @param genreId идентификатор жанра.
     * @return жанр.
     */
    public Genre getGenreById(long genreId) {
        log.debug("Получение жанра с id = {}", genreId);

        Optional<Genre> genreOptional =  this.genreStorage.getGenreById(genreId);
        if (genreOptional.isEmpty()) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден", genreId));
        }

        return genreOptional.get();
    }
}
