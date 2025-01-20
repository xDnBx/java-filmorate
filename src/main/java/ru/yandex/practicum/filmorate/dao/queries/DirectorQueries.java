package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для режиссёров.
 */
public final class DirectorQueries {
    /**
     * SQL-запрос для создания нового режиссёра.
     */
    public static final String CREATE_DIRECTOR_QUERY =
            """
            INSERT INTO
                    directors (name)
            VALUES
                    (?)
            """;

    /**
     * SQL-запрос для получения списка всех режиссёров.
     */
    public static final String GET_ALL_DIRECTORS_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    directors
            """;

    /**
     * SQL-запрос для получения режиссёра по его идентификатору.
     */
    public static final String GET_DIRECTOR_BY_ID_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    directors
            WHERE
                    id = ?
            """;

    /**
     * SQL-запрос для обновления режиссёра.
     */
    public static final String UPDATE_DIRECTOR_QUERY =
            """
            UPDATE
                    directors
            SET
                    name = ?
            WHERE
                    id = ?
            """;

    /**
     * SQL-запрос для удаления режиссёра.
     */
    public static final String DELETE_DIRECTOR_QUERY =
            """
            DELETE FROM
                    directors
            WHERE
                    id = ?
            """;
}
