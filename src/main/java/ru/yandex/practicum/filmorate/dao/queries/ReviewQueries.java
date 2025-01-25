package ru.yandex.practicum.filmorate.dao.queries;

/**
 * Вспомогательный класс, содержащий SQL-запросы для отзывов.
 */
public final class ReviewQueries {

    //region Отзывы

    /**
     * SQL-запрос для добавления нового отзыва к фильму.
     */
    public static final String CREATE_REVIEW_QUERY =
            """
            INSERT INTO
                    reviews (content, is_positive, user_id, film_id)
            VALUES
                    (?, ?, ?, ?)
            """;

    /**
     * Получить список отзывов.
     */
    public static final String GET_REVIEWS_QUERY =
            """
            WITH
                        positive AS (SELECT review_id, COUNT(*) as count FROM review_likes WHERE is_liked = true GROUP BY review_id),
                        negative AS (SELECT review_id, COUNT(*) as count FROM review_likes WHERE is_liked = false GROUP BY review_id)
            SELECT
                        r.id,
                        r.content,
                        r.is_positive,
                        r.user_id,
                        r.film_id,
                        COALESCE(p.count, 0) - COALESCE(n.count, 0) as useful
            FROM
            	        reviews r
              LEFT JOIN positive p ON p.review_id = r.id
              LEFT JOIN negative n ON n.review_id = r.id
            WHERE
                        1 = 1
            ORDER BY
                        useful DESC
            LIMIT
                        ?
            """;

    /**
     * Получить отзыв по его идентификатору.
     */
    public static final String GET_REVIEW_BY_ID_QUERY =
            """
            WITH
                        positive AS (SELECT review_id, COUNT(*) as count FROM review_likes WHERE is_liked = true GROUP BY review_id),
                        negative AS (SELECT review_id, COUNT(*) as count FROM review_likes WHERE is_liked = false GROUP BY review_id)
            SELECT
                        r.id,
                        r.content,
                        r.is_positive,
                        r.user_id,
                        r.film_id,
                        COALESCE(p.count, 0) - COALESCE(n.count, 0) as useful
            FROM
            	        reviews r
              LEFT JOIN positive p ON p.review_id = r.id
              LEFT JOIN negative n ON n.review_id = r.id
            WHERE
                        r.id = ?
            """;

    /**
     * SQL-запрос для обновления отзыва.
     */
    public static final String UPDATE_REVIEW_QUERY =
            """
            UPDATE
            	    reviews
            SET
                    content = ?,
                    is_positive = ?
            WHERE
            	    id = ?
            """;

    /**
     * SQL-запрос для удаления отзыва.
     */
    public static final String DELETE_REVIEW_QUERY =
            """
            DELETE FROM
            	    reviews
            WHERE
            	    id = ?
            """;

    //endregion

    //region Лайки

    /**
     * SQL-запрос для добавления лайка к отзыву.
     */
    public static final String ADD_LIKE_TO_REVIEW_QUERY =
            """
            INSERT INTO
                    review_likes (review_id, user_id, is_liked)
            VALUES
                    (?, ?, ?)
            """;

    /**
     * SQL-запрос для проверки существования лайка у отзыва.
     */
    public static final String IS_LIKE_FOR_REVIEW_EXISTS_QUERY =
            """
            SELECT
                    EXISTS (SELECT 1 FROM review_likes WHERE review_id = ? AND user_id = ?)
            """;

    /**
     * SQL-запрос для обновления лайка у отзыва.
     */
    public static final String UPDATE_LIKE_FOR_REVIEW_QUERY =
            """
            UPDATE
                    review_likes
            SET
                    is_liked = ?
            WHERE
                    review_id = ?
                AND
                    user_id = ?
            """;

    /**
     * SQL-запрос для удаления лайка у отзыва.
     */
    public static final String REMOVE_LIKE_FROM_REVIEW_QUERY =
            """
            DELETE FROM
                    review_likes
            WHERE
                    review_id = ?
                AND
                    user_id = ?
            """;

    //endregion
}
