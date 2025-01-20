package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для пользователей.
 */
public final class UserQueries {

    //region Пользователи

    /**
     * SQL-запрос для создания нового пользователя.
     */
    public static final String CREATE_USER_QUERY =
            """
            INSERT INTO
                    users (email, login, name, birthday)
            VALUES
                    (?, ?, ?, ?)
            """;

    /**
     * SQL-запрос для получения списка всех пользователей.
     */
    public static final String GET_ALL_USERS_QUERY =
            """
            SELECT
                    id,
                    email,
                    login,
                    name,
                    birthday
            FROM
                    users
            """;

    /**
     * SQL-запрос для получения пользователя по его идентификатору.
     */
    public static final String GET_USER_BY_ID_QUERY =
            """
            SELECT
                    id,
                    email,
                    login,
                    name,
                    birthday
            FROM
                    users
            WHERE
                    id = ?
            """;

    /**
     * SQL-запрос для обновления пользователя.
     */
    public static final String UPDATE_USER_QUERY =
            """
            UPDATE
                    users
            SET
                    email = ?,
                    login = ?,
                    name = ?,
                    birthday = ?
            WHERE
                    id = ?
            """;

    /**
     * SQL-запрос для удаления пользователя.
     */
    public static final String DELETE_USER_QUERY =
            """
            DELETE FROM
                    users
            WHERE
                    id = ?
            """;

    //endregion

    //region Друзья

    /**
     * SQL-запрос для добавления пользователя в друзья.
     */
    public static final String ADD_FRIEND_TO_USER_QUERY =
            """
            INSERT INTO
                    friends(user_id, friend_id)
            VALUES
                    (?, ?)
            """;

    /**
     * SQL-запрос для получения списка друзей пользователя.
     */
    public static final String GET_USER_FRIENDS_QUERY =
            """
            SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
            FROM
                    users u
               JOIN friends f ON f.friend_id = u.id
            WHERE
                    f.user_id = ?
            """;

    /**
     * SQL-запрос для получения списка общих друзей двух пользователей.
     */
    public static final String GET_COMMON_FRIENDS_QUERY =
            """
            SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
            FROM
                    users u
               JOIN friends f1 ON f1.friend_id = u.id
               JOIN friends f2 ON f2.friend_id = u.id
            WHERE
                    f1.user_id = ?
                AND
                    f2.user_id = ?
            """;

    /**
     * SQL-запрос для удаления пользователя из друзей.
     */
    public static final String DELETE_USER_FROM_FRIENDS_QUERY =
            """
            DELETE FROM
                    friends
            WHERE
                    user_id = ?
                AND
                    friend_id = ?
            """;

    //endregion
}
