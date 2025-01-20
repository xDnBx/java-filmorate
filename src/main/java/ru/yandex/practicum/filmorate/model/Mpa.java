package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Оценка Ассоциации кинокомпаний.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Mpa {
    /**
     * Идентификатор оценки Ассоциации кинокомпаний.
     */
    private long id;

    /**
     * Название оценки Ассоциации кинокомпаний.
     */
    private String name;
}