package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для оценок Ассоциации кинокомпаний.
 */
public final class MpaQueries {
    /**
     * SQL-запрос для получения списка всех оценок Ассоциации кинокомпаний.
     */
    public static final String GET_ALL_MPA_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    mpa
            """;

    /**
     * SQL-запрос для получения оценки Ассоциации кинокомпаний по её идентификатору.
     */
    public static final String GET_MPA_BY_ID_QUERY =
            """
            SELECT
                    id,
                    name
            FROM
                    mpa
            WHERE
                    id = ?
            """;
}
