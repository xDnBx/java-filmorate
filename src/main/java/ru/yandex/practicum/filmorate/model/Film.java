package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"mpa", "genres", "likes"})
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть больше нуля")
    private Integer duration;

    @NotNull
    private Mpa mpa;

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    private Collection<Director> directors = new HashSet<>();

    private Set<Long> likes = new HashSet<>();
}