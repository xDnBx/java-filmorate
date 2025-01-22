package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequestDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public final class ReviewMapper {
    public static Review mapToReview(CreateReviewRequestDto dto) {
        return Review.builder()
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .build();
    }

    public static Review mapToReview(UpdateReviewRequestDto dto) {
        return Review.builder()
                .id(dto.getReviewId())
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .build();
    }

    public static ReviewDto mapToReviewDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
    }

    public static Collection<ReviewDto> mapToReviewDtoCollection(Collection<Review> reviewCollection) {
        return reviewCollection.stream().map(ReviewMapper::mapToReviewDto).toList();
    }
}
