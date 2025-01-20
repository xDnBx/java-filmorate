package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequestDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

/**
 * Контроллер для запросов отзывов.
 */
@RequestMapping("/reviews")
@RequiredArgsConstructor
@RestController
@Slf4j
public final class ReviewController {
    /**
     * Сервис для работы с отзывами.
     */
    private final ReviewService reviewService;

    //region Отзыва

    /**
     * Создать новый отзыв.
     *
     * @param dto трансферный объект для запроса на создание нового отзыва.
     * @return отзыв.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody CreateReviewRequestDto dto) {
        log.info("Запрос на добавление нового отзыва от пользователя с id = {} к фильму с id = {}", dto.getUserId(), dto.getFilmId());
        return ReviewMapper.mapToReviewDto(this.reviewService.createReview(ReviewMapper.mapToReview(dto)));
    }

    /**
     * Получить список отзывов.
     *
     * @param filmId идентификатор фильма.
     * @param count  количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    @GetMapping()
    public Collection<ReviewDto> getReviews(@RequestParam(required = false) Long filmId, @RequestParam(defaultValue = "10") int count) {
        if (filmId == null) {
            log.info("Запрос на получение {} отзывов", count);
            return ReviewMapper.mapToReviewDtoCollection(this.reviewService.getReviews(count));
        }

        log.info("Запрос на получение {} отзывов к фильму с id = {}", count, filmId);
        return ReviewMapper.mapToReviewDtoCollection(this.reviewService.getFilmReviews(filmId, count));

    }

    /**
     * Получить отзыв по его идентификатору.
     *
     * @param reviewId идентификатор отзыва.
     * @return отзыв
     */
    @GetMapping("{reviewId}")
    public ReviewDto getReview(@PathVariable long reviewId) {
        log.info("Запрос на получение отзыва с id = {}", reviewId);
        return ReviewMapper.mapToReviewDto(this.reviewService.getReviewById(reviewId));
    }

    /**
     * Обновить отзыв.
     *
     * @param dto трансферный объект для запроса на обновление отзыва.
     * @return отзыв.
     */
    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequestDto dto) {
        log.info("Запрос на обновление отзыва с id = {}", dto.getReviewId());
        return ReviewMapper.mapToReviewDto(this.reviewService.updateReview(ReviewMapper.mapToReview(dto)));
    }

    /**
     * Удалить отзыв.
     *
     * @param reviewId идентификатор отзыва.
     */
    @DeleteMapping("{reviewId}")
    public void deleteReview(@PathVariable long reviewId) {
        log.info("Запрос на удаление отзыва с id = {}", reviewId);
        this.reviewService.deleteReview(reviewId);
    }

    //endregion

    //region Лайки

    /**
     * Добавить лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     */
    @PutMapping("{reviewId}/like/{userId}")
    public void addLike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Запрос на добавление лайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        this.reviewService.addLikeToReview(reviewId, userId, true);
    }

    /**
     * Добавить дизлайк к отзыву.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     */
    @PutMapping("{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Запрос на добавление дизлайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        this.reviewService.addLikeToReview(reviewId, userId, false);
    }

    /**
     * Удалить лайк у отзыва.
     *
     * @param reviewId  идентификатор отзыва.
     * @param userId    идентификатор пользователя.
     */
    @DeleteMapping("{reviewId}/like/{userId}")
    public void deleteLike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Запрос на удаление лайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        this.reviewService.removeLikeFromReview(reviewId, userId);
    }

    /**
     * Удалить дизлайк у отзыва.
     *
     * @param reviewId  идентификатор отзыва.
     * @param userId    идентификатор пользователя.
     */
    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Запрос на удаление дизлайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        this.reviewService.removeLikeFromReview(reviewId, userId);
    }

    //endregion
}
