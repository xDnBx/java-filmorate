package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmRating {
    private Integer id;
    private Integer filmId;
    private Integer rating;
}
