package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.BooleanMapper;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.dao.queries.FilmQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище фильмов, основанное на БД.
 */
@Repository
@Slf4j
public class FilmRepository extends BaseRepository implements FilmStorage {

    //region Фильмы

    /**
     * Создать новый фильм.
     *
     * @param film фильм.
     * @return фильм.
     */
    @Override
    public Film createFilm(Film film) {
        try {
            long id = this.insert(FilmQueries.CREATE_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
            film.setId(id);

            addDirectorsToFilm(film.getId(), film.getDirectors());
            addGenresToFilm(film.getId(), film.getGenres());

            log.debug("Фильм {} успешно добавлен с id = {}", film.getName(), id);

            film.setId(id);
            film.setDirectors(this.getFilmDirectors(id));
            film.setGenres(this.getFilmGenres(id));

            return film;
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении фильма {}: [{}] {}", film.getName(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список всех фильмов.
     *
     * @return список всех фильмов.
     */
    @Override
    public Collection<Film> getAllFilms() {
        try {
            Collection<Film> films = jdbc.query(FilmQueries.GET_ALL_FILMS_QUERY, FilmMapper::mapToFilm);

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка всех фильмов: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список популярных фильмов, отсортированный по количеству лайков.
     *
     * @param count   количество фильмов, которые необходимо получить.
     * @param genreId идентификатор жанра, по которому необходимо произвести фильтрацию, если предоставлен.
     * @param year    год выпуска фильма, по которому необходимо произвести фильтрацию, если предоставлен.
     * @return список фильмов.
     */
    @Override
    public Collection<Film> getPopularFilms(long count, Long genreId, Integer year) {
        try {
            Collection<Film> films;
            if (genreId != null && year != null) {
                films = this.findMany(FilmQueries.GET_POPULAR_FILMS_QUERY.replace("1 = 1", String.format("fg.genre_id = %d AND EXTRACT (YEAR FROM f.release_date) = %d", genreId, year)), FilmMapper::mapToFilm, count);
            } else if (genreId != null) {
                films = this.findMany(FilmQueries.GET_POPULAR_FILMS_QUERY.replace("1 = 1", String.format("fg.genre_id = %d", genreId)), FilmMapper::mapToFilm, count);
            } else if (year != null) {
                films = this.findMany(FilmQueries.GET_POPULAR_FILMS_QUERY.replace("1 = 1", String.format("EXTRACT (YEAR FROM f.release_date) = %d", year)), FilmMapper::mapToFilm, count);
            } else {
                films = this.findMany(FilmQueries.GET_POPULAR_FILMS_QUERY, FilmMapper::mapToFilm, count);
            }

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка популярных фильмов: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список фильмов, рекомендуемых для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список фильмов.
     */
    @Override
    public Collection<Film> getRecommendedFilms(long userId) {
        try {
            Collection<Film> films = this.findMany(FilmQueries.GET_RECOMMENDED_FILMS_QUERY, FilmMapper::mapToFilm, userId, userId, userId);

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка рекомендуемых фильмов для пользователя с id = {}: [{}] {}", userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить фильм по его идентификатору.
     *
     * @param filmId идентификатор фильма.
     * @return фильм.
     */
    @Override
    public Optional<Film> getFilmById(long filmId) {
        try {
            return this.findOne(FilmQueries.GET_FILM_BY_ID_QUERY, FilmMapper::mapToFilm, filmId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список фильмов режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     * @param sortBy     поле, по которому необходимо отсортировать список фильмов.
     * @return список фильмов.
     */
    @Override
    public Collection<Film> getDirectorFilms(long directorId, String sortBy) {
        try {
            String query;
            if (sortBy.equalsIgnoreCase("likes")) {
                query = FilmQueries.GET_DIRECTOR_FILMS_SORTED_BY_LIKES_QUERY;
            } else if (sortBy.equalsIgnoreCase("year")) {
                query = FilmQueries.GET_DIRECTOR_FILMS_SORTED_BY_YEARS_QUERY;
            } else {
                throw new NotFoundException(String.format("Запрос для сортировки по %s не найден", sortBy));
            }

            Collection<Film> films = this.findMany(query, FilmMapper::mapToFilm, directorId);

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка фильмов режиссёра с id = {}: [{}] {}", directorId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список общих с другом фильмов.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     * @return список фильмов.
     */
    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        try {
            Collection<Film> films = this.findMany(FilmQueries.GET_COMMON_FILMS_QUERY, FilmMapper::mapToFilm, userId, friendId);

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка общих фильмов пользователей с идентификаторами {}, {}: [{}] {}", userId, friendId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список фильмов, подходящих под условие поиска.
     *
     * @param query поисковая строка.
     * @param by    список полей, по которым необходимо произвести поиск.
     * @return список фильмов.
     */
    @Override
    public Collection<Film> searchFilms(String query, String by) {
        try {
            Collection<Film> films;

            if (by.isBlank()) {
                films = this.findMany(FilmQueries.SEARCH_FILMS_QUERY, FilmMapper::mapToFilm);
            }

            String[] fields = by.split(",");
            if (fields.length == 1) {
                if (fields[0].equalsIgnoreCase("title")) {
                    films = this.findMany(FilmQueries.SEARCH_FILMS_QUERY.replace("1 = 1", "f.name ILIKE '%" + query + "%'"), FilmMapper::mapToFilm);
                } else if (fields[0].equalsIgnoreCase("director")) {
                    films = this.findMany(FilmQueries.SEARCH_FILMS_QUERY.replace("1 = 1", "d.name ILIKE '%" + query + "%'"), FilmMapper::mapToFilm);
                } else {
                    throw new InternalServerException("Не удалось определить поле для поиска");
                }
            } else if (fields.length == 2) {
                List<String> sortedFields = Arrays.stream(fields).sorted().toList();
                if (sortedFields.getFirst().equalsIgnoreCase("director") && sortedFields.getLast().equalsIgnoreCase("title")) {
                    films = this.findMany(FilmQueries.SEARCH_FILMS_QUERY.replace("1 = 1", "f.name ILIKE '%" + query + "%' OR d.name ILIKE '%" + query + "%'"), FilmMapper::mapToFilm);
                } else {
                    throw new InternalServerException("Некорректный набор полей для поиска");
                }
            } else {
                throw new InternalServerException("Некорректное количество полей для поиска");
            }

            films.forEach(film -> {
                film.setDirectors(this.getFilmDirectors(film.getId()));
                film.setGenres(this.getFilmGenres(film.getId()));
            });

            return films;
        } catch (Throwable ex) {
            log.error("Ошибка при поиске фильмов: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Обновить фильм.
     *
     * @param film фильм.
     */
    @Override
    public void updateFilm(Film film) {
        try {
            this.update(FilmQueries.UPDATE_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate().toString(), film.getDuration(), film.getMpa().getId(), film.getId());

            addDirectorsToFilm(film.getId(), film.getDirectors());
            addGenresToFilm(film.getId(), film.getGenres());

            log.debug("Фильм с id = {} успешно обновлен", film.getId());
        } catch (Throwable ex) {
            log.error("Ошибка при обновлении фильма с id = {}: [{}] {}", film.getId(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить фильм.
     *
     * @param filmId идентификатор фильма.
     */
    @Override
    public void deleteFilm(long filmId) {
        try {
            this.delete(FilmQueries.DELETE_FILM_QUERY, filmId);

            log.debug("Фильм с id = {} успешно удален", filmId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    //endregion

    // region Режиссёры

    /**
     * Добавить фильму режиссёров.
     *
     * @param filmId    идентификатор фильма.
     * @param directors список режиссёров.
     */
    private void addDirectorsToFilm(long filmId, Collection<Director> directors) {
        try {
            this.delete(FilmQueries.CLEAR_DIRECTORS_FROM_FILM_QUERY, filmId);
            this.insert(FilmQueries.ADD_DIRECTOR_TO_FILM_QUERY, directors, (d) -> List.of(filmId, d.getId()));
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении режиссёров для фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список режиссёров фильма по идентификатору фильма.
     *
     * @param filmId идентификатор фильма.
     * @return список режиссёров.
     */
    @Override
    public Collection<Director> getFilmDirectors(long filmId) {
        try {
            return this.findMany(FilmQueries.GET_DIRECTORS_OF_FILM_QUERY, DirectorMapper::mapToDirector, filmId);
        } catch (Exception ex) {
            log.error("Ошибка при получении списка режиссёров для фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    // endregion

    // region Жанры

    /**
     * Добавить фильму жанры.
     *
     * @param filmId идентификатор фильма.
     * @param genres список жанров.
     */
    private void addGenresToFilm(long filmId, Collection<Genre> genres) {
        try {
            this.delete(FilmQueries.CLEAR_GENRES_FROM_FILM_QUERY, filmId);
            this.insert(FilmQueries.ADD_GENRE_TO_FILM_QUERY, genres, (g) -> List.of(filmId, g.getId()));
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении жанров для фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список жанров фильма по идентификатору фильма.
     *
     * @param filmId идентификатор фильма.
     * @return список жанров.
     */
    @Override
    public Collection<Genre> getFilmGenres(long filmId) {
        try {
            return this.findMany(FilmQueries.GET_FILM_GENRES_QUERY, GenreMapper::mapToGenre, filmId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка жанров для фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    // endregion

    //region Лайки

    /**
     * Добавить фильму пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @Override
    public void addLikeToFilm(long filmId, long userId) {
        try {
            this.insert(FilmQueries.ADD_LIKE_TO_FILM_QUERY, filmId, userId);
            log.debug("Пользователь с id = {} поставил лайк фильму id = {}", userId, filmId);
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении лайка пользователя с id = {} для фильма с id = {}: [{}] {}", userId, filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Проверить существование пользовательского лайка.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     * @return признак, существует ли пользовательский лайк.
     */
    @Override
    public boolean isLikeExist(long filmId, long userId) {
        try {
            return this.findOne(FilmQueries.IS_LIKE_FOR_FILM_EXISTS_QUERY, BooleanMapper::mapToBoolean, filmId, userId).orElse(false);
        } catch (Throwable ex) {
            log.error("Ошибка при проверке существования лайка пользователя с id = {} для фильма с id = {}: [{}] {}", userId, filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить у фильма пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @Override
    public void removeLikeFromFilm(long filmId, long userId) {
        try {
            this.delete(FilmQueries.REMOVE_LIKE_FROM_FILM_QUERY, filmId, userId);
            log.debug("Пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении лайка пользователя с id = {} у фильма с id = {}: [{}] {}", userId, filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    //endregion
}