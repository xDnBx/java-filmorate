package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmRepository implements FilmStorage {
    private static final String GET_ALL_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration," +
            " m.id AS mpa_id, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            " duration = ?, mpa_id = ? WHERE id = ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_ID_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration," +
            " f.mpa_id, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id WHERE f.id = ?";
    private static final String FIND_GENRES_QUERY = "SELECT g.id, g.name FROM films_genres AS f" +
            " JOIN genres AS g ON f.genre_id = g.id WHERE f.film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

    private final JdbcTemplate jdbc;

    @Override
    public Collection<Film> getAllFilms() {
        try {
            List<Film> films = jdbc.query(GET_ALL_QUERY, FilmMapper::mapToFilm);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            return films;
        } catch (RuntimeException e) {
            log.error("Ошибка при получении списка фильмов");
            throw new InternalServerException("Ошибка при получении списка фильмов");
        }
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);

            Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(id);
            addGenresToFilm(film);
            log.info("Фильм с id = {} успешно добавлен!", film.getId());
            return film;
        } catch (Exception e) {
            log.error("Ошибка при добавлении фильма");
            throw new InternalServerException("Ошибка при добавлении фильма");
        }
    }

    @Override
    public Film updateFilm(Film newFilm) {
        try {
            jdbc.update(UPDATE_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate().toString(),
                    newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
            addGenresToFilm(newFilm);
            log.info("Обновление фильма с id = {} прошло успешно!", newFilm.getId());
            return getFilmById(newFilm.getId());
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма");
            throw new InternalServerException("Ошибка при обновлении фильма");
        }
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbc.queryForObject(FIND_ID_QUERY, ((resultSet, rowNum) -> {
                Film film = FilmMapper.mapToFilm(resultSet, rowNum);
                film.setGenres(getFilmGenres(id));
                return film;
            }), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске фильма");
            throw new NotFoundException("Фильм с данным id не найден");
        }
    }

    @Override
    public void addLike(Long id, Long userId) {
        try {
            jdbc.update(INSERT_LIKE_QUERY, id, userId);
            log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, id);
        } catch (Exception e) {
            log.error("Ошибка при добавлении лайка пользователя");
            throw new InternalServerException("Ошибка при добавлении лайка пользователя");
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        try {
            jdbc.update(DELETE_LIKE_QUERY, id, userId);
            log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, id);
        } catch (Exception e) {
            log.error("Ошибка при удалении лайка пользователя");
            throw new InternalServerException("Ошибка при удалении лайка пользователя");
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        String insert = "";
        if (genreId != null && year == null) {
            insert = "JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = " + genreId;
        }
        if (genreId == null && year != null) {
            insert = "WHERE YEAR(f.release_date) = " + year;
        }
        if (genreId != null && year != null) {
            insert = "JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = " + genreId +
                    " AND YEAR(f.release_date) = " + year;
        }

        String getPopularQuery = "SELECT l.film_id, COUNT(l.film_id), f.id, f.name, f.description," +
                " f.release_date, f.duration, m.id AS mpa_id, m.name AS mpa_name FROM likes AS l" +
                " JOIN films AS f ON l.film_id = f.id JOIN mpa AS m ON f.mpa_id = m.id " + insert +
                " GROUP BY l.film_id ORDER BY COUNT(l.film_id) DESC LIMIT ?";

        try {
            List<Film> films = jdbc.query(getPopularQuery, FilmMapper::mapToFilm, count);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            return films;
        } catch (Exception e) {
            log.error("Ошибка при получении списка популярных фильмов");
            throw new InternalServerException("Ошибка при получении списка популярных фильмов");
        }
    }

    private void addGenresToFilm(Film film) {
        try {
            LinkedHashSet<Genre> genres = film.getGenres();
            jdbc.update(DELETE_GENRES_QUERY, film.getId());
            jdbc.batchUpdate(INSERT_GENRE_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    Genre genre = (Genre) genres.toArray()[i];
                    preparedStatement.setLong(1, film.getId());
                    preparedStatement.setInt(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        } catch (Exception e) {
            log.error("Ошибка при добавлении жанров");
            throw new InternalServerException("Ошибка при добавлении жанров");
        }
    }

    private LinkedHashSet<Genre> getFilmGenres(Long id) {
        try {
            return new LinkedHashSet<>(Objects.requireNonNull(jdbc.query(FIND_GENRES_QUERY, GenreMapper::mapToGenre,
                    id)));
        } catch (Exception e) {
            log.error("Ошибка при получении списка жанров");
            throw new InternalServerException("Ошибка при получении списка жанров");
        }
    }
}