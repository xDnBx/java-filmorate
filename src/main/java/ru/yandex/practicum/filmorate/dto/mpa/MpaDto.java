package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.Builder;
import lombok.Data;

/**
 * Трансферный объект для сущности "Оценка Ассоциации кинокомпаний".
 */
@Builder(toBuilder = true)
@Data
public class MpaDto {
    /**
     * Идентификатор оценки.
     */
    private long id;

    /**
     * Название оценки.
     */
    private String name;
}
