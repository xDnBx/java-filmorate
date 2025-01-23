package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Трансферный объект для запроса на создание нового отзыва.
 */
@Data
public class CreateReviewRequestDto {
    /**
     * Содержимое отзыва.
     */
    @NotBlank
    private String content;

    /**
     * Признак является ли отзыв положительным.
     */
    @NotNull
    private Boolean isPositive;

    /**
     * Идентификатор пользователя, оставившего отзыв.
     */
    @NotNull
    private Long userId;

    /**
     * Идентификатор фильма, к которому был оставлен отзыв.
     */
    @NotNull
    private Long filmId;
}