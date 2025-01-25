package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для событий.
 */
public final class EventQueries {
    /**
     * SQL-запрос для создания нового события.
     */
    public static String CREATE_EVENT_QUERY =
            """
            INSERT INTO
                    events (user_id, event_type, operation, entity_id)
            VALUES
                    (?, ?, ?, ?)
            """;

    /**
     * SQL-запрос для получения событий пользователя.
     */
    public static String GET_USER_EVENTS_QUERY =
            """
            SELECT
                    id,
                    timestamp,
                    user_id,
                    event_type,
                    operation,
                    entity_id
            FROM
            	    events
            WHERE
                    user_id = ?
            """;
}
