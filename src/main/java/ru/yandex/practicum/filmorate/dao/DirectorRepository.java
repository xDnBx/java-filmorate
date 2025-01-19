package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class DirectorRepository implements DirectorStorage {

    private static final String CREATE_DIRECTOR_QUERY = "INSERT INTO directors (name) VALUES (?)";

    private static final String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";

    private static final String GET_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";

    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE id = ?";

    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?";

    private static final String FIND_DIRECTOR_QUERY_BY_FILM_ID = "SELECT d.id, d.name  FROM DIRECTORS d, FILMS_DIRECTORS fd WHERE fd.FILM_ID = ? AND d.ID = fd.DIRECTOR_ID";

    private final JdbcTemplate jdbc;

    @Override
    public Director createDirector(Director director) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(CREATE_DIRECTOR_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, director.getName());
                return ps;
            }, keyHolder);

            Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            director.setId(id);

            log.info("Режиссёр успешно добавлен с идентификатором {}", id);

            return director;
        } catch (Exception ex) {
            log.error("Ошибка при добавлении режиссёра", ex);
            throw new InternalServerException("Ошибка при добавлении режиссёра");
        }
    }

    @Override
    public Collection<Director> getAllDirectors() {
        try {
            return jdbc.query(GET_ALL_DIRECTORS_QUERY, DirectorMapper::mapToDirector);
        } catch (Exception ex) {
            log.error("Ошибка при получении списка режиссёров", ex);
            throw new InternalServerException("Ошибка при получении списка режиссёров");
        }
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        try {
            return jdbc.queryForObject(GET_DIRECTOR_BY_ID_QUERY, DirectorMapper::mapToDirector, directorId);
        } catch (EmptyResultDataAccessException ex) {
            log.error(String.format("Ошибка при получении режиссёра с идентификатором %d", directorId), ex);
            throw new NotFoundException(String.format("Режиссёр с идентификатором %d не найден", directorId));
        }
    }

    @Override
    public Director updateDirector(Director newDirector) {
        getDirectorById(newDirector.getId());

        try {
            jdbc.update(UPDATE_DIRECTOR_QUERY, newDirector.getName(), newDirector.getId());

            log.info("Обновление режиссёра с идентификатором {} прошло успешно", newDirector.getId());
            return getDirectorById(newDirector.getId());
        } catch (Exception e) {
            log.error(String.format("Ошибка при обновлении режиссёра с идентификатором %d", newDirector.getId()), e);
            throw new InternalServerException("Ошибка при обновлении режиссёра");
        }
    }

    @Override
    public void deleteDirector(Integer directorId) {
        try {
            jdbc.update(DELETE_DIRECTOR_QUERY, directorId);
            log.info("Режиссёр с идентификатором {} успешно удалён", directorId);
        } catch (Exception ex) {
            log.error(String.format("Ошибка при удалении режиссёра с идентификатором %d", directorId), ex);
            throw new InternalServerException("Ошибка при удалении режиссёра");
        }
    }

    @Override
    public Collection<Director> getDirectorByFilmId(Long filmId) {
        try {
            log.info("Фильмы по режиссерам найдены {} успешно", filmId);
            return jdbc.query(FIND_DIRECTOR_QUERY_BY_FILM_ID, DirectorMapper::mapToDirector, filmId);
        } catch (Exception ex) {
            log.error(String.format("Ошибка при поиски режиссёра по id фильма %d", filmId), ex);
            throw new InternalServerException("Ошибка при удалении режиссёра");
        }
    }
}
