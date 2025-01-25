package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для фильмов.
 */
public final class FilmQueries {

    // region Фильмы

    /**
     * SQL-запрос для создания нового фильма
     */
    public static final String CREATE_FILM_QUERY =
            """
                    INSERT INTO
                            films (name, description, release_date, duration, mpa_id)
                    VALUES
                            (?, ?, ?, ?, ?)
                    """;

    /**
     * SQL-запрос для получения списка всех фильмов.
     */
    public static final String GET_ALL_FILMS_QUERY =
            """
                    SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            m.id AS mpa_id,
                            m.name AS mpa_name
                    FROM
                            films f
                       JOIN mpa m ON m.id = f.mpa_id
                    """;

    /**
     * SQL-запрос для получения списка фильмов, подходящих под условие поиска.
     */
    public static final String SEARCH_FILMS_QUERY =
            """
                    SELECT
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                m.id AS mpa_id,
                                m.name AS mpa_name
                    FROM
                                films f
                           JOIN mpa m ON m.id = f.mpa_id
                      LEFT JOIN film_directors fd ON fd.film_id = f.id
                      LEFT JOIN directors d ON d.id = fd.director_id
                      LEFT JOIN film_likes fl ON fl.film_id = f.id
                    WHERE
                                1 = 1
                    GROUP BY
                                f.id
                    ORDER BY
                                COUNT(fl.id) DESC
                    """;

    /**
     * SQL-запрос для получения списка популярных фильмов, отсортированных по количеству лайков.
     */
    public static final String GET_POPULAR_FILMS_QUERY =
            """
                    SELECT
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                m.id AS mpa_id,
                                m.name AS mpa_name
                    FROM
                                films f
                           JOIN mpa m ON m.id = f.mpa_id
                      LEFT JOIN film_genres fg ON fg.film_id = f.id
                      LEFT JOIN film_likes fl ON fl.film_id = f.id
                    WHERE
                                1 = 1
                    GROUP BY
                                f.id
                    ORDER BY
                                COUNT(fl.id) DESC
                    LIMIT
                                ?
                    """;

    /**
     * SQL-запрос для получения списка рекомендуемых фильмов.
     */
    public static final String GET_RECOMMENDED_FILMS_QUERY =
            """
                    SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            m.id AS mpa_id,
                            m.name AS mpa_name
                    FROM
                            films f
                       JOIN mpa m ON m.id = f.mpa_id
                       JOIN film_likes fl ON fl.film_id = f.id
                    WHERE
                            fl.film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)
                        AND
                            fl.user_id IN (
                                SELECT
                                        user_id
                                FROM
                                        film_likes
                                WHERE
                                        film_id IN (SELECT film_id FROM film_likes WHERE user_id = ?)
                                    AND
                                        user_id != ?
                                GROUP BY
                                        user_id
                                ORDER BY
                                        COUNT(film_id) DESC
                                LIMIT
                                        10
                            )
                    """;

    /**
     * SQL-запрос для получения фильма по его идентификатору.
     */
    public static final String GET_FILM_BY_ID_QUERY =
            """
                    SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            f.mpa_id,
                            m.name AS mpa_name
                    FROM
                            films f
                       JOIN mpa m ON m.id = f.mpa_id
                    WHERE
                            f.id = ?
                    """;

    /**
     * SQL-запрос для получения списка фильмов режиссёра, отсортированного по количеству лайков.
     */
    public static final String GET_DIRECTOR_FILMS_SORTED_BY_LIKES_QUERY =
            """
                    SELECT
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                m.id AS mpa_id,
                                m.name AS mpa_name,
                                COUNT(fl.user_id) as likes_count
                    FROM
                                films f
                           JOIN mpa m ON m.id = f.mpa_id
                           JOIN film_directors fd ON fd.film_id = f.id
                      LEFT JOIN film_likes fl ON fl.film_id = f.id
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

    /**
     * SQL-запрос для получения списка фильмов режиссёра, отсортированного по году выпуска фильма.
     */
    public static final String GET_DIRECTOR_FILMS_SORTED_BY_YEARS_QUERY =
            """
                    SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            m.id AS mpa_id,
                            m.name AS mpa_name
                    FROM
                            films f
                       JOIN mpa m ON m.id = f.mpa_id
                       JOIN film_directors fd ON fd.film_id = f.id
                    WHERE
                            fd.director_id = ?
                    ORDER BY
                            EXTRACT(YEAR FROM f.release_date)
                    """;

    /**
     * SQL-запрос для получения общих с другом фильмов.
     */
    public static final String GET_COMMON_FILMS_QUERY =
            """
                    SELECT
                                f.id,
                                f.name,
                                f.description,
                                f.release_date,
                                f.duration,
                                m.id AS mpa_id,
                                m.name AS mpa_name,
                                COUNT(fl.user_id) AS likes_count
                    FROM
                                films f
                      LEFT JOIN film_likes fl ON fl.film_id = f.id
                      LEFT JOIN mpa m ON m.id = f.mpa_id
                    WHERE
                                fl.user_id = ?
                            AND
                                f.id IN (SELECT film_id FROM film_likes WHERE user_id = ?)
                    GROUP BY
                                f.id
                    ORDER BY
                                likes_count DESC
                    """;

    /**
     * SQL-запрос для обновления фильма.
     */
    public static final String UPDATE_FILM_QUERY =
            """
                    UPDATE
                            films
                    SET
                            name = ?,
                            description = ?,
                            release_date = ?,
                            duration = ?,
                            mpa_id = ?
                    WHERE
                            id = ?
                    """;

    /**
     * SQL-запрос для удаления фильма.
     */
    public static final String DELETE_FILM_QUERY =
            """
                    DELETE FROM
                            films
                    WHERE
                            id = ?
                    """;

    // endregion

    // region Режиссёры

    /**
     * SQL-запрос для добавления фильму режиссёра.
     */
    public static final String ADD_DIRECTOR_TO_FILM_QUERY =
            """
                    INSERT INTO
                            film_directors (film_id, director_id)
                    VALUES
                            (?, ?)
                    """;

    /**
     * SQL-запрос для получения списка режиссёров фильма.
     */
    public static final String GET_DIRECTORS_OF_FILM_QUERY =
            """
                    SELECT
                            d.id,
                            d.name
                    FROM
                            directors d
                       JOIN film_directors fd ON fd.director_id = d.id
                    WHERE
                            fd.film_id = ?
                    """;

    public static final String GET_FILMS_DIRECTORS_QUERY =
            """
                    SELECT
                            d.id AS director_id,
                            d.name AS director_name
                    FROM
                            directors d
                       JOIN film_directors fd ON fd.director_id = d.id
                    WHERE
                            fd.film_id IN
                    """;

    /**
     * SQL-запрос для удаления у фильма всех режиссёров.
     */
    public static final String CLEAR_DIRECTORS_FROM_FILM_QUERY =
            """
                    DELETE FROM
                            film_directors
                    WHERE
                            film_id = ?
                    """;

    // endregion

    // region Жанры

    /**
     * SQL-запрос для добавления фильму жанра.
     */
    public static final String ADD_GENRE_TO_FILM_QUERY =
            """
                    INSERT INTO
                            film_genres (film_id, genre_id)
                    VALUES
                            (?, ?)
                    """;

    /**
     * SQL-запрос для получения списка жанров фильма.
     */
    public static final String GET_FILM_GENRES_QUERY =
            """
                    SELECT
                            g.id,
                            g.name
                    FROM
                            film_genres fg
                       JOIN genres g ON g.id = fg.genre_id
                    WHERE
                            fg.film_id = ?
                    """;

    /**
     * SQL-запрос для получения списка жанров фильмов.
     */
    public static final String GET_FILMS_GENRES_QUERY =
            """
                    SELECT
                            g.id AS genre_id,
                            g.name AS genre_name
                    FROM
                            film_genres fg
                       JOIN genres g ON g.id = fg.genre_id
                    WHERE
                            fg.film_id IN
                    """;

    /**
     * SQL-запрос для удаления у фильма всех жанров.
     */
    public static final String CLEAR_GENRES_FROM_FILM_QUERY =
            """
                    DELETE FROM
                            film_genres
                    WHERE
                            film_id = ?
                    """;

    // endregion

    // region Лайки

    /**
     * SQL-запрос для добавления фильму пользовательского лайка.
     */
    public static final String ADD_LIKE_TO_FILM_QUERY =
            """
                    INSERT INTO
                            film_likes (film_id, user_id)
                    VALUES
                            (?, ?)
                    """;

    /**
     * SQL-запрос для проверки существования лайка у фильма.
     */
    public static final String IS_LIKE_FOR_FILM_EXISTS_QUERY =
            """
                    SELECT
                            EXISTS (SELECT 1 FROM film_likes WHERE film_id = ? AND user_id = ?)
                    """;

    /**
     * SQL-запрос для получения пользовательского лайка.
     */
    public static final String GET_USER_LIKE_QUERY =
            """
                    SELECT
                            id,
                            film_id,
                            user_id
                    FROM
                            film_likes
                    WHERE
                            film_id = ?
                        AND
                            user_id = ?
                    """;

    /**
     * SQL-запрос для удаления у фильма пользовательского лайка.
     */
    public static final String REMOVE_LIKE_FROM_FILM_QUERY =
            """
                    DELETE FROM
                            film_likes
                    WHERE
                            film_id = ?
                        AND
                            user_id = ?
                    """;

    // endregion
}
