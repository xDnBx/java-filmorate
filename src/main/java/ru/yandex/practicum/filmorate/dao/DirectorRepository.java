package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.queries.DirectorQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище режиссёров.
 */
@Repository
@Slf4j
public class DirectorRepository extends BaseRepository implements DirectorStorage {
    /**
     * Создать нового режиссёра.
     *
     * @param director режиссёр.
     * @return режиссёр.
     */
    @Override
    public Director createDirector(Director director) {
        try {
            long id = this.insert(DirectorQueries.CREATE_DIRECTOR_QUERY, director.getName());
            director.setId(id);

            log.debug("Режиссёр {} успешно добавлен с id = {}", director.getName(), id);
            return director;
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении режиссёра: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список всех режиссёров.
     *
     * @return список режиссёров.
     */
    @Override
    public Collection<Director> getAllDirectors() {
        try {
            return this.findMany(DirectorQueries.GET_ALL_DIRECTORS_QUERY, DirectorMapper::mapToDirector);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка режиссёров: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить режиссёра по его идентификатору.
     *
     * @param directorId идентификатор режиссёра.
     * @return режиссёр.
     */
    @Override
    public Optional<Director> getDirectorById(long directorId) {
        try {
            return this.findOne(DirectorQueries.GET_DIRECTOR_BY_ID_QUERY, DirectorMapper::mapToDirector, directorId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении режиссёра с id = {}: [{}] {}", directorId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();

        }
    }

    /**
     * Обновить режиссёра.
     *
     * @param director режиссёр.
     */
    @Override
    public void updateDirector(Director director) {
        try {
            this.update(DirectorQueries.UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
            log.debug("Режиссёр с id = {} успешно обновлен", director.getId());
        } catch (Throwable ex) {
            log.error("Ошибка при обновлении режиссёра с id = {}: [{}] {}", director.getId(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     */
    @Override
    public void deleteDirector(long directorId) {
        try {
            this.delete(DirectorQueries.DELETE_DIRECTOR_QUERY, directorId);
            log.debug("Режиссёр с id = {} успешно удалён", directorId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении режиссёра с id = {}: [{}] {}", directorId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }
}
