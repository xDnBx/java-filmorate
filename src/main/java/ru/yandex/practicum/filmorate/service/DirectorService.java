package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с режиссёрами.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class DirectorService {
    /**
     * Хранилище режиссёров.
     */
    private final DirectorStorage directorStorage;

    /**
     * Создать нового режиссёра.
     *
     * @param director режиссёр.
     * @return режиссёр.
     */
    public Director createDirector(Director director) {
        log.debug("Добавление нового режиссёра с именем {}", director.getName());
        return this.directorStorage.createDirector(director);
    }

    /**
     * Получить список всех режиссёров.
     *
     * @return список режиссёров.
     */
    public Collection<Director> getAllDirectors() {
        log.debug("Получение списка всех режиссёров");
        return this.directorStorage.getAllDirectors();
    }

    /**
     * Получить режиссёра по его идентификатору.
     *
     * @param directorId идентификатор режиссёра.
     * @return режиссёр.
     */
    public Director getDirectorById(long directorId) {
        log.debug("Получение режиссёра с id = {}", directorId);

        Optional<Director> directorOptional = this.directorStorage.getDirectorById(directorId);
        if (directorOptional.isEmpty()) {
            throw new NotFoundException(String.format("Режиссёр с id = %d не найден", directorId));
        }

        return directorOptional.get();
    }

    /**
     * Обновить режиссёра.
     *
     * @param director режиссёр.
     * @return режиссёр.
     */
    public Director updateDirector(Director director) {
        throwIfDirectorNotFound(director.getId());
        log.debug("Обновление режиссёра с id = {}", director.getId());

        this.directorStorage.updateDirector(director);
        return this.getDirectorById(director.getId());
    }

    /**
     * Удалить режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     */
    public void deleteDirector(long directorId) {
        throwIfDirectorNotFound(directorId);
        log.debug("Удаление режиссёра с id = {}", directorId);

        this.directorStorage.deleteDirector(directorId);
    }

    //region Facilities

    /**
     * Выбросить исключение, если режиссёр не найден.
     *
     * @param directorId идентификатор режиссёра.
     */
    private void throwIfDirectorNotFound(long directorId) {
        if (this.directorStorage.getDirectorById(directorId).isEmpty()) {
            throw new NotFoundException(String.format("Режиссёр с id = %d не найден", directorId));
        }
    }

    //endregion
}
