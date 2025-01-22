package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

/**
 * Фильм.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = {"mpa", "genres"})
public class Film {
    /**
     * Идентификатор фильма.
     */
    private long id;

    /**
     * Название фильма.
     */
    private String name;

    /**
     * Описание фильма.
     */
    private String description;

    /**
     * Дата релиза.
     */
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах.
     */
    private int duration;

    /**
     * Оценка Ассоциации кинокомпаний.
     */
    private Mpa mpa;

    /**
     * Список режиссёров фильма.
     */
    private Collection<Genre> genres = new HashSet<>();

    /**
     * Список жанров фильма.
     */
    private Collection<Director> directors = new HashSet<>();
}