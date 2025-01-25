package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для жанров.
 */
public final class GenreQueries {
    /**
     * SQL-запрос для получения списка всех жанров.
     */
    public static final String GET_ALL_GENRES_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    genres
            """;

    /**
     * SQL-запрос для получения жанра по его идентификатору.
     */
    public static final String GET_GENRE_BY_ID_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    genres
            WHERE
                    id = ?
            """;
}
