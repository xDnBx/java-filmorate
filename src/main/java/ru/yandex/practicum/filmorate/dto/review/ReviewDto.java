package ru.yandex.practicum.filmorate.dto.review;

import lombok.Builder;
import lombok.Data;

/**
 * Трансферный объект для сущности "Отзыв".
 */
@Builder(toBuilder = true)
@Data
public class ReviewDto {
    /**
     * Идентификатор отзыва.
     */
    private long reviewId;

    /**
     * Содержимое отзыва.
     */
    private String content;

    /**
     * Признак является ли отзыв положительным.
     */
    private Boolean isPositive;

    /**
     * Идентификатор пользователя, оставившего отзыв.
     */
    private long userId;

    /**
     * Идентификатор фильма, к которому был оставлен отзыв.
     */
    private long filmId;

    /**
     * Рейтинг полезности отзыва.
     */
    private int useful;
}
