package ru.yandex.practicum.filmorate.dao.mappers;

import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public class ReviewMapper {

    public static ResponseReviewDTO mapToResponseReviewDTO(Review review) {
        return ResponseReviewDTO.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
    }

    public static List<ResponseReviewDTO> mapToResponseReviewDTOList(List<Review> reviewList) {
        return reviewList.stream().map(ReviewMapper::mapToResponseReviewDTO).toList();
    }
}
