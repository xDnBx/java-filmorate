package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewRating {
    private Integer id;
    private Integer reviewId;
    private Integer rating;
}
