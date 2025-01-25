package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.validation.constraint.DirectorDtoCollectionConstraint;
import ru.yandex.practicum.filmorate.validation.constraint.GenreDtoCollectionConstraint;
import ru.yandex.practicum.filmorate.validation.constraint.MpaDtoConstraint;
import ru.yandex.practicum.filmorate.validation.constraint.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

/**
 * Трансферный объект для запроса на создание нового фильма.
 */
@Data
public final class CreateFilmRequestDto {
    /**
     * Название фильма.
     */
    @NotBlank(message = "Название фильма не может быть пустым или состоять только из пробелов")
    private String name;

    /**
     * Описание фильма.
     */
    @NotBlank(message = "Описание фильма не может быть пустым или состоять только из пробелов")
    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;

    /**
     * Дата релиза.
     */
    @ReleaseDateConstraint
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах.
     */
    @Positive(message = "Продолжительность должна быть больше нуля")
    private int duration;

    /**
     * Оценка Ассоциации кинокомпаний.
     */
    @MpaDtoConstraint
    private MpaDto mpa;

    /**
     * Список режиссёров фильма.
     */
    @DirectorDtoCollectionConstraint
    private final Collection<DirectorDto> directors = new HashSet<>();

    /**
     * Список жанров фильма.
     */
    @GenreDtoCollectionConstraint
    private final Collection<GenreDto> genres = new HashSet<>();
}
