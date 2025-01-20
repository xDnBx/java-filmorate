package ru.yandex.practicum.filmorate.dto.director;

import lombok.Builder;
import lombok.Data;

/**
 * Трансферный объект для сущности "Режиссёр".
 */
@Builder(toBuilder = true)
@Data
public class DirectorDto {
    /**
     * Идентификатор режиссёра.
     */
    private long id;

    /**
     * Имя режиссёра.
     */
    private String name;
}
