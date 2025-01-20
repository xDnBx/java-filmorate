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
import ru.yandex.practicum.filmorate.dao.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmRepository implements FilmStorage {

    // region Queries

    private static final String GET_ALL_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration," +
            " m.id AS mpa_id, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id";

    private static final String GET_DIRECTOR_FILMS_SORTED_BY_LIKES =
            """
                    SELECT
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                m.id AS mpa_id,
                                m.name AS mpa_name,
                                COUNT(l.user_id) as likes_count
                    FROM
                                films f
                           JOIN mpa m ON m.id = f.mpa_id
                           JOIN films_directors fd ON fd.film_id = f.id
                      LEFT JOIN likes l ON l.film_id = f.id
                    WHERE
                                fd.director_id = ?
                    GROUP BY
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                mpa_id,
                                mpa_name
                    ORDER BY
                                likes_count DESC
                    """;

    private static final String GET_DIRECTOR_FILMS_SORTED_BY_YEARS =
            """
                    SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            m.id AS mpa_id,
                            m.name AS mpa_name,
                    FROM
                            films f
                       JOIN mpa m ON m.id = f.mpa_id
                       JOIN films_directors fd ON fd.film_id = f.id
                    WHERE
                            fd.director_id = ?
                    ORDER BY
                            EXTRACT(YEAR FROM f.release_date)
                    """;

    private static final String GET_COMMON_FILMS = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "m.id AS mpa_id, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
            "FROM films as f " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE l.user_id = ? AND f.id IN (SELECT l.film_id FROM likes WHERE user_id = ?)" +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC";

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

    private static final String EXIST_LIKE_QUERY = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ? ";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String DELETE_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String DELETE_FILM_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String DELETE_REVIEWS_QUERY = "DELETE FROM reviews WHERE film_id = ?";
    private static final String DELETE_FILMS_RATING_QUERY = "DELETE FROM films_rating WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String ADD_DIRECTOR_TO_FILM_QUERY = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";

    private static final String GET_FILM_DIRECTORS_QUERY = "SELECT d.id, d.name FROM directors d" +
            " JOIN films_directors fd ON fd.director_id = d.id WHERE fd.film_id = ?";

    private static final String REMOVE_DIRECTORS_FROM_FILM = "DELETE FROM films_directors WHERE film_id = ?";

    private static final String FIND_MOVIES_QUERY_BY_TITLE = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
            "FROM FILMS f " +
            "WHERE f.NAME ILIKE ";

    private static final String FIND_MOVIES_QUERY_BY_DIRECTOR = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
            "FROM FILMS f, " +
            "     FILMS_DIRECTORS fd, " +
            "     DIRECTORS d " +
            "WHERE D.NAME ILIKE ";

    private static final String FIND_MOVIES_QUERY_BY_DIRECTOR_AND_TITLE = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
            "FROM FILMS f, " +
            "     FILMS_DIRECTORS fd, " +
            "     DIRECTORS d " +
            "WHERE F.ID = FD.FILM_ID " +
            "  AND FD.DIRECTOR_ID = D.ID " +
            "  AND D.NAME ILIKE ";
    // endregion

    private final JdbcTemplate jdbc;

    @Override
    public Collection<Film> getAllFilms() {
        try {
            List<Film> films = jdbc.query(GET_ALL_QUERY, FilmMapper::mapToFilm);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            films.forEach(film -> film.setDirectors(getFilmDirectors(film.getId())));
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
            addDirectorsToFilm(film);
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
            addDirectorsToFilm(newFilm);
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
                film.setDirectors(getFilmDirectors(id));
                return film;
            }), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске фильма");
            throw new NotFoundException("Фильм с данным id не найден");
        }
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (existLikeFilmAndUser(id, userId)) return;
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
    public List<Film> getCommonFilms(Long userId1, Long userId2) {
        try {
            List<Film> films = jdbc.query(GET_COMMON_FILMS, FilmMapper::mapToFilm, userId1, userId2);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            films.forEach(film -> film.setDirectors(getFilmDirectors(film.getId())));
            return films;
        } catch (Exception e) {
            log.error("Ошибка при получении списка общих фильмов пользователей: {}, {}", userId1, userId2);
            throw new InternalServerException("Ошибка при получении списка общих фильмов");
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        String insert = "";
        if (genreId != null && year == null) {
            insert = "LEFT JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = " + genreId;
        }
        if (genreId == null && year != null) {
            insert = "WHERE YEAR(f.release_date) = " + year;
        }
        if (genreId != null && year != null) {
            insert = "LEFT JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = " + genreId +
                    " AND YEAR(f.release_date) = " + year;
        }

        String getPopularQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id AS mpa_id," +
                " m.name AS mpa_name FROM films AS f" +
                " LEFT JOIN likes AS l ON l.film_id = f.id LEFT JOIN mpa AS m ON f.mpa_id = m.id " + insert +
                " GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

        try {
            List<Film> films = jdbc.query(getPopularQuery, FilmMapper::mapToFilm, count);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            films.forEach(film -> film.setDirectors(getFilmDirectors(film.getId())));
            return films;
        } catch (Exception e) {
            log.error("Ошибка при получении списка популярных фильмов");
            throw new InternalServerException("Ошибка при получении списка популярных фильмов");
        }
    }

    @Override
    public Collection<Film> getDirectorFilms(Integer directorId, String sortBy) {
        try {
            String query;
            if (sortBy.equalsIgnoreCase("likes")) {
                query = GET_DIRECTOR_FILMS_SORTED_BY_LIKES;
            } else if (sortBy.equalsIgnoreCase("year")) {
                query = GET_DIRECTOR_FILMS_SORTED_BY_YEARS;
            } else {
                throw new NotFoundException(String.format("Запрос для сортировки по %s не найден", sortBy));
            }

            List<Film> films = jdbc.query(query, FilmMapper::mapToFilm, directorId);
            films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
            films.forEach(film -> film.setDirectors(getFilmDirectors(film.getId())));
            return films;
        } catch (Exception ex) {
            log.error(String.format("Ошибка при получении списка фильмов режиссёра с идентификатором %d", directorId), ex);
            throw new InternalServerException("Ошибка при получении списка фильмов режиссёра");
        }
    }

    @Override
    public void deleteFilm(Long filmId) {
        try {
            jdbc.update(DELETE_GENRES_QUERY, filmId);
            jdbc.update(DELETE_FILM_LIKE_QUERY, filmId);
            jdbc.update(DELETE_REVIEWS_QUERY, filmId);
            jdbc.update(DELETE_FILMS_RATING_QUERY, filmId);
            jdbc.update(DELETE_QUERY, filmId);
            log.info("Фильм с id = {} удален", filmId);
        } catch (Exception e) {
            log.error("Ошибка при удалении фильма");
            throw new InternalServerException("Ошибка при удалении фильма");
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

    private void addDirectorsToFilm(Film film) {
        try {
            Collection<Director> directors = film.getDirectors();
            jdbc.update(REMOVE_DIRECTORS_FROM_FILM, film.getId());
            jdbc.batchUpdate(ADD_DIRECTOR_TO_FILM_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    Director director = (Director) directors.toArray()[i];
                    preparedStatement.setLong(1, film.getId());
                    preparedStatement.setInt(2, director.getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        } catch (Exception ex) {
            log.error(String.format("Ошибка при добавлении режиссёров для фильма с идентификатором %d", film.getId()), ex);
            throw new InternalServerException("Ошибка при добавлении режиссёров");
        }
    }

    private Collection<Director> getFilmDirectors(Long id) {
        try {
            return new HashSet<>(Objects.requireNonNull(jdbc.query(GET_FILM_DIRECTORS_QUERY, DirectorMapper::mapToDirector, id)));
        } catch (Exception ex) {
            log.error(String.format("Ошибка при получении списка режиссёров для фильма с идентификатором %d", id), ex);
            throw new InternalServerException("Ошибка при получении списка режиссёров");
        }
    }

    @Override
    public List<Film> findByNameFilm(String requestedMovieTitleToSearch) {
        try {
            List<Film> films = convertToFilms(jdbc.queryForList(prepareSqlForFindMoviesByDirectorAndTitle(requestedMovieTitleToSearch, null, null)));
            log.info("Количество найденных фильмов: {}", films.size());
            return films;
        } catch (Exception e) {
            log.error("Ошибка при получении списка найденных фильмов: {}", e.getMessage());
            throw new InternalServerException("Ошибка при получении списка найденных фильмов");
        }
    }

    @Override
    public List<Film> findByNameFilmAndTitleDirector(String requestedMovieTitleToSearch, String title, String director) {
        try {
            String preparedQuery = prepareSqlForFindMoviesByDirectorAndTitle(requestedMovieTitleToSearch, title, director);

            List<Film> films = convertToFilms(jdbc.queryForList(preparedQuery));
            log.info("Количество найденных фильмов: {}", films.size());
            return films;
        } catch (Exception e) {
            log.error("Ошибка при получении списка найденных фильмов: {}", e.getMessage());
            throw new InternalServerException("Ошибка при получении списка найденных фильмов");
        }
    }

    private static String prepareSqlForFindMoviesByDirectorAndTitle(String requestedMovieTitleToSearch, String director, String title) {
        requestedMovieTitleToSearch = requestedMovieTitleToSearch.toUpperCase();
        String preparedQuery = null;

        if (title != null && director == null) {
            preparedQuery = FIND_MOVIES_QUERY_BY_TITLE + "'" + "%" + requestedMovieTitleToSearch + "%" + "'" + " ";
        }
        if (director != null && title == null) {
            preparedQuery = FIND_MOVIES_QUERY_BY_DIRECTOR + "'" + "%" + requestedMovieTitleToSearch + "%" + "'" +
                    "  AND FD.DIRECTOR_ID=D.ID " +
                    "  AND f.ID=FD.FILM_ID";
        }
        if (director != null && title != null) {
            preparedQuery = FIND_MOVIES_QUERY_BY_DIRECTOR_AND_TITLE + "'" + "%" + requestedMovieTitleToSearch + "%" + "'" +
                    "OR f.NAME ILIKE " + "'" + "%" + requestedMovieTitleToSearch + "%" + "'";
        }
        if (preparedQuery == null) {
            return "";
        }
        preparedQuery += " group by f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
                "order by f.NAME ";
        return preparedQuery;
    }


    private List<Film> convertToFilms(List<Map<String, Object>> results) {
        List<Film> films = new ArrayList<>();
        for (Map<String, Object> row : results) {
            Film film = Film.builder()
                    .id(((Number) row.get("ID")).longValue())
                    .name((String) row.get("NAME"))
                    .description((String) row.get("DESCRIPTION"))
                    .releaseDate(((Date) row.get("RELEASE_DATE")).toLocalDate())
                    .duration((Integer) row.get("DURATION"))
                    .mpa(new Mpa((Integer) row.get("MPA_ID"), ""))
                    .build();
            films.add(film);
        }
        return films;
    }

    private boolean existLikeFilmAndUser(Long filmId, Long userId) {
        try {
            Integer count = jdbc.queryForObject(EXIST_LIKE_QUERY, Integer.class, filmId, userId);
            return count > 0;
        } catch (Exception e) {
            throw new InternalServerException("Ошибка при проверке лайка пользователя");
        }
    }


}