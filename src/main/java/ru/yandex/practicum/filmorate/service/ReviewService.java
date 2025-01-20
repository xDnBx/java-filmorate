package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public final class ReviewService {
    /**
     * Хранилище событий.
     */
    private final EventStorage eventStorage;

    /**
     * Хранилище фильмов.
     */
    private final FilmStorage filmStorage;

    /**
     * Хранилище отзывов.
     */
    private final ReviewStorage reviewStorage;

    /**
     * Хранилище пользователей.
     */
    private final UserStorage userStorage;

    //region Отзывы

    /**
     * Создать новый отзыв.
     *
     * @param review отзыв.
     * @return отзыв.
     */
    public Review createReview(Review review) {
        throwIfUserNotFound(review.getUserId());
        throwIfFilmNotFound(review.getFilmId());
        log.debug("Добавление нового отзыва от пользователя с id = {} к фильму с id = {}", review.getUserId(), review.getFilmId());

        Review created = this.reviewStorage.createReview(review);
        this.eventStorage.createEvent(created.getUserId(), EventType.REVIEW, EventOperation.ADD, created.getId());

        return created;
    }

    /**
     * Получить список отзывов.
     *
     * @param count количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    public Collection<Review> getReviews(int count) {
        log.debug("Получение {} отзывов", count);
        return this.reviewStorage.getReviews(count);
    }

    /**
     * Получить список отзывов к фильму.
     *
     * @param filmId идентификатор фильма.
     * @param count  количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    public Collection<Review> getFilmReviews(long filmId, int count) {
        throwIfFilmNotFound(filmId);
        log.debug("Получение {} отзывов к фильму с id = {}", count, filmId);

        return this.reviewStorage.getFilmReviews(filmId, count);
    }

    /**
     * Получить отзыв по его идентификатору.
     *
     * @param reviewId идентификатор отзыва.
     * @return отзыв
     */
    public Review getReviewById(long reviewId) {
        log.debug("Получение отзыва с id = {}", reviewId);

        Optional<Review> reviewOptional = this.reviewStorage.getReviewById(reviewId);
        if (reviewOptional.isEmpty()) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", reviewId));
        }

        return reviewOptional.get();
    }

    /**
     * Обновить отзыв.
     *
     * @param review отзыв.
     * @return отзыв.
     */
    public Review updateReview(Review review) {
        throwIfReviewNotFound(review.getId());
        throwIfUserNotFound(review.getUserId());
        throwIfFilmNotFound(review.getFilmId());
        log.debug("Обновление отзыва с id = {}", review.getId());

        this.reviewStorage.updateReview(review);

        Review updatedReview = this.getReviewById(review.getId());
        this.eventStorage.createEvent(updatedReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, updatedReview.getId());

        return updatedReview;
    }

    /**
     * Удалить отзыв.
     *
     * @param reviewId идентификатор отзыва.
     */
    public void deleteReview(long reviewId) {
        throwIfReviewNotFound(reviewId);
        log.debug("Удаление отзыва с id = {}", reviewId);

        Review review = this.getReviewById(reviewId);

        this.reviewStorage.deleteReview(reviewId);
        this.eventStorage.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
    }

    //endregion

    //region Лайки

    /**
     * Добавить лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     * @param isLiked  признак, нравится ли пользователю отзыв.
     */
    public void addLikeToReview(long reviewId, long userId, boolean isLiked) {
        throwIfReviewNotFound(reviewId);
        throwIfUserNotFound(userId);

        if (!this.reviewStorage.isLikeForReviewExist(reviewId, userId)) {
            if (isLiked) {
                log.debug("Добавление лайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
            } else {
                log.debug("Добавление дизлайка от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
            }

            this.reviewStorage.addLikeToReview(reviewId, userId, isLiked);
            return;
        }

        if (isLiked) {
            log.debug("Смена дизлайка на лайк от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        } else {
            log.debug("Смена лайка на дизлайк от пользователя с id = {} к отзыву с id = {}", userId, reviewId);
        }

        this.reviewStorage.updateLikeForReview(reviewId, userId, isLiked);
    }

    /**
     * Удалить лайк у отзыва.
     *
     * @param reviewId  идентификатор отзыва.
     * @param userId    идентификатор пользователя.
     */
    public void removeLikeFromReview(long reviewId, long userId) {
        throwIfReviewNotFound(reviewId);
        throwIfUserNotFound(userId);
        log.debug("Удаления лайка от пользователя с id = {} у отзыва с id = {}", userId, reviewId);

        this.reviewStorage.removeLikeFromReview(reviewId, userId);
    }

    //endregion

    //region Facilities

    /**
     * Выбросить исключение, если фильм не найден.
     *
     * @param filmId идентификатор фильма.
     */
    private void throwIfFilmNotFound(long filmId) {
        if (this.filmStorage.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }

    /**
     * Выбросить исключение, если отзыв не найден.
     *
     * @param reviewId идентификатор отзыва.
     */
    private void throwIfReviewNotFound(long reviewId) {
        if (this.reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", reviewId));
        }
    }

    /**
     * Выбросить исключение, если пользователей не найден.
     *
     * @param userId идентификатор пользователя.
     */
    private void throwIfUserNotFound(long userId) {
        if (this.userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //endregion
}