package ru.yandex.practicum.filmorate.dto.genre;

import lombok.Data;

/**
 * Трансферный объект для сущности "Жанр".
 */
@Data
public final class GenreDto {
    /**
     * Идентификатор жанра.
     */
    private long id;

    /**
     * Название жанра.
     */
    private String name;
}
