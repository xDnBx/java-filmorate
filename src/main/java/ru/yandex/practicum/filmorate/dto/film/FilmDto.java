package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Трансферный объект для сущности "Фильм".
 */
@Builder(toBuilder = true)
@Data
public final class FilmDto {
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
    private MpaDto mpa;

    /**
     * Список режиссёров фильма.
     */
    private Collection<DirectorDto> directors;

    /**
     * Список жанров фильма.
     */
    private Collection<GenreDto> genres;
}
