package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Жанр.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Genre {
    /**
     * Идентификатор жанра.
     */
    private long id;

    /**
     * Название жанра.
     */
    private String name;
}