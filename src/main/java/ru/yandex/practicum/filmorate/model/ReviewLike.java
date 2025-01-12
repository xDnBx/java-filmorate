package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewLike {
    private Integer id;
    private Integer reviewId;
    private Integer userId;
    private Boolean isLiked;
}
