package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища режиссёров.
 */
public interface DirectorStorage {
    /**
     * Создать нового режиссёра.
     *
     * @param director режиссёр.
     * @return режиссёр.
     */
    Director createDirector(Director director);

    /**
     * Получить список всех режиссёров.
     *
     * @return список режиссёров.
     */
    Collection<Director> getAllDirectors();

    /**
     * Получить режиссёра по его идентификатору.
     *
     * @param directorId идентификатор режиссёра.
     * @return режиссёр.
     */
    Optional<Director> getDirectorById(long directorId);

    /**
     * Обновить режиссёра.
     *
     * @param director режиссёр.
     */
    void updateDirector(Director director);

    /**
     * Удалить режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     */
    void deleteDirector(long directorId);
}
