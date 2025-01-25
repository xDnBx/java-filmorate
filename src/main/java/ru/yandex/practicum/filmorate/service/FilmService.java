package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с фильмами.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class FilmService {
    /**
     * Хранилище режиссёров.
     */
    private final DirectorStorage directorStorage;

    /**
     * Хранилище событий.
     */
    private final EventStorage eventStorage;

    /**
     * Хранилище фильмов.
     */
    private final FilmStorage filmStorage;

    /**
     * Хранилище пользователей.
     */
    private final UserStorage userStorage;

    //region Фильмы

    /**
     * Создать новый фильм.
     *
     * @param film фильм.
     * @return фильм.
     */
    public Film createFilm(Film film) {
        log.debug("Добавление нового фильма: {}", film.getName());
        return this.filmStorage.createFilm(film);
    }

    /**
     * Получить список всех фильмов.
     *
     * @return список фильмов.
     */
    public Collection<Film> getAllFilms() {
        log.debug("Получение списка всех фильмов");
        return this.filmStorage.getAllFilms();
    }

    /**
     * Получить список популярных фильмов, отсортированный по количеству лайков.
     *
     * @param count   количество фильмов, которые необходимо получить.
     * @param genreId идентификатор жанра, по которому необходимо произвести фильтрацию, если предоставлен.
     * @param year    год выпуска фильма, по которому необходимо произвести фильтрацию, если предоставлен.
     * @return список фильмов.
     */
    public Collection<Film> getPopularFilms(long count, Long genreId, Integer year) {
        log.debug("Получение списка из {} популярных фильмов", count);
        return this.filmStorage.getPopularFilms(count, genreId, year);
    }

    /**
     * Получить список фильмов, рекомендуемых для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список фильмов.
     */
    public Collection<Film> getRecommendedFilms(long userId) {
        throwIfUserNotFound(userId);
        log.debug("Получение списка рекомендуемых фильмов для пользователя с id = {} ", userId);

        return this.filmStorage.getRecommendedFilms(userId);
    }

    /**
     * Получить фильм по его идентификатору.
     *
     * @param filmId идентификатор фильма.
     * @return фильм.
     */
    public Film getFilmById(long filmId) {
        log.debug("Получение фильма с id = {}", filmId);

        Optional<Film> filmOptional = this.filmStorage.getFilmById(filmId);
        if (filmOptional.isEmpty()) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }

        Film film = filmOptional.get();
        film.setDirectors(this.filmStorage.getFilmDirectors(film.getId()));
        film.setGenres(this.filmStorage.getFilmGenres(film.getId()));

        return film;
    }

    /**
     * Получить список фильмов режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     * @param sortBy     поле, по которому необходимо отсортировать список фильмов.
     * @return список фильмов.
     */
    public Collection<Film> getDirectorFilms(long directorId, SortBy sortBy) {
        throwIfDirectorNotFound(directorId);
        log.debug("Получение фильмов режиссёра с id = {}. Сортировка по полю {}", directorId, sortBy);

        return this.filmStorage.getDirectorFilms(directorId, sortBy);
    }

    /**
     * Получить список общих с другом фильмов.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     * @return список фильмов.
     */
    public Collection<Film> getCommonFilm(long userId, long friendId) {
        throwIfUserNotFound(userId);
        throwIfUserNotFound(friendId);
        log.debug("Получение списка общих фильмов для пользователей с id = {} и id = {}", userId, friendId);

        return this.filmStorage.getCommonFilms(userId, friendId);
    }

    /**
     * Получить список фильмов, подходящих под условие поиска.
     *
     * @param query поисковая строка.
     * @param by    список полей, по которым необходимо произвести поиск.
     * @return список фильмов.
     */
    public Collection<Film> searchFilms(String query, String by) {
        return this.filmStorage.searchFilms(query, by);
    }

    /**
     * Обновить фильм.
     *
     * @param film фильм.
     * @return фильм.
     */
    public Film updateFilm(Film film) {
        this.throwIfFilmNotFound(film.getId());
        log.debug("Обновление фильма с id = {}", film.getId());

        this.filmStorage.updateFilm(film);
        return this.getFilmById(film.getId());
    }

    /**
     * Удалить фильм.
     *
     * @param filmId идентификатор фильма.
     */
    public void deleteFilm(Long filmId) {
        this.throwIfFilmNotFound(filmId);
        log.debug("Удаление фильма с id = {}", filmId);

        this.filmStorage.deleteFilm(filmId);
    }

    //endregion

    //region Лайки

    /**
     * Добавить фильму пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    public void addLikeToFilm(long filmId, long userId) {
        this.throwIfFilmNotFound(filmId);
        this.throwIfUserNotFound(userId);

        if (this.filmStorage.isLikeExist(filmId, userId)) {
            this.eventStorage.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
            return;
        }

        log.debug("Добавление лайка фильму с id = {} от пользователя с id = {}", filmId, userId);

        this.filmStorage.addLikeToFilm(filmId, userId);
        this.eventStorage.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    /**
     * Удалить у фильма пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    public void removeLikeFromFilm(long filmId, long userId) {
        this.throwIfFilmNotFound(filmId);
        this.throwIfUserNotFound(userId);

        if (!this.filmStorage.isLikeExist(filmId, userId)) {
            this.eventStorage.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
            return;
        }

        log.debug("Удаление лайка фильму с id = {} от пользователя с id = {}", filmId, userId);

        this.filmStorage.removeLikeFromFilm(filmId, userId);
        this.eventStorage.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    //endregion

    //region Facilities

    /**
     * Выбросить исключение, если пользователь не найден.
     *
     * @param directorId идентификатор пользователя.
     */
    private void throwIfDirectorNotFound(long directorId) {
        if (this.directorStorage.getDirectorById(directorId).isEmpty()) {
            throw new NotFoundException(String.format("Режиссёр с id = %d не найден", directorId));
        }
    }

    /**
     * Выбросить исключение, если фильм не найден.
     *
     * @param filmId идентификатор фильма.
     */
    private void throwIfFilmNotFound(long filmId) {
        if (this.filmStorage.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }

    /**
     * Выбросить исключение, если пользователей не найден.
     *
     * @param userId идентификатор пользователя.
     */
    private void throwIfUserNotFound(long userId) {
        if (this.userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //endregion
}