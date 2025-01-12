package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateReviewDTO {
    @NotNull
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
}
