package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Отзыв.
 */
@Data
@Builder
public class Review {
    /**
     * Идентификатор отзыва.
     */
    private long id;

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
