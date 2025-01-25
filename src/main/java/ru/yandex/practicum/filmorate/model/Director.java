package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Режиссёр.
 */
@Builder(toBuilder = true)
@Data
public class Director {
    public Director(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Идентификатор режиссёра.
     */
    private long id;

    /**
     * Имя режиссёра.
     */
    private String name;
}
