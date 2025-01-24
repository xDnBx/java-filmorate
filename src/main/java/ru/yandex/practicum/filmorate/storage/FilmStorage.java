package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища фильмов.
 */
public interface FilmStorage {

    // region Фильмы

    /**
     * Создать новый фильм.
     *
     * @param film фильм.
     * @return фильм.
     */
    Film createFilm(Film film);

    /**
     * Получить список всех фильмов.
     *
     * @return список фильмов.
     */
    Collection<Film> getAllFilms();

    /**
     * Получить список популярных фильмов, отсортированный по количеству лайков.
     *
     * @param count   количество фильмов, которые необходимо получить.
     * @param genreId идентификатор жанра, по которому необходимо произвести фильтрацию, если предоставлен.
     * @param year    год выпуска фильма, по которому необходимо произвести фильтрацию, если предоставлен.
     * @return список фильмов.
     */
    Collection<Film> getPopularFilms(long count, Long genreId, Integer year);

    /**
     * Получить список фильмов, рекомендуемых для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список фильмов.
     */
    Collection<Film> getRecommendedFilms(long userId);

    /**
     * Получить фильм по его идентификатору.
     *
     * @param filmId идентификатор фильма.
     * @return фильм.
     */
    Optional<Film> getFilmById(long filmId);

    /**
     * Получить список фильмов режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     * @param sortBy     поле, по которому необходимо отсортировать список фильмов.
     * @return список фильмов.
     */
    Collection<Film> getDirectorFilms(long directorId, SortBy sortBy);

    /**
     * Получить список общих с другом фильмов.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     * @return список фильмов.
     */
    Collection<Film> getCommonFilms(long userId, long friendId);

    /**
     * Получить список фильмов, подходящих под условие поиска.
     *
     * @param query поисковая строка.
     * @param by    список полей, по которым необходимо произвести поиск.
     * @return список фильмов.
     */
    Collection<Film> searchFilms(String query, String by);

    /**
     * Обновить фильм.
     *
     * @param film фильм.
     */
    void updateFilm(Film film);

    /**
     * Удалить фильм.
     *
     * @param filmId идентификатор фильма.
     */
    void deleteFilm(long filmId);

    // endregion

    //region Режиссёры

    /**
     * Получить список режиссёров фильма по идентификатору фильма.
     *
     * @param filmId идентификатор фильма.
     * @return список режиссёров.
     */
    Collection<Director> getFilmDirectors(long filmId);

    //endregion

    //region Жанры

    /**
     * Получить список жанров фильма по идентификатору фильма.
     *
     * @param filmId идентификатор фильма.
     * @return список жанров.
     */
    Collection<Genre> getFilmGenres(long filmId);

    //endregion

    //region Лайки

    /**
     * Добавить фильму пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    void addLikeToFilm(long filmId, long userId);

    /**
     * Проверить существование пользовательского лайка.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     * @return признак, существует ли пользовательский лайк.
     */
    boolean isLikeExist(long filmId, long userId);

    /**
     * Удалить у фильма пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    void removeLikeFromFilm(long filmId, long userId);

    //endregion
}