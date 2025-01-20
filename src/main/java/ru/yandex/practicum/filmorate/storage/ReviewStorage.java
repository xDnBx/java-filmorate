package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища отзывов.
 */
public interface ReviewStorage {

    //region Отзывы

    /**
     * Создать новый отзыв.
     *
     * @param review отзыв.
     * @return отзыв.
     */
    Review createReview(Review review);

    /**
     * Получить список отзывов.
     *
     * @param count количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    Collection<Review> getReviews(int count);

    /**
     * Получить список отзывов к фильму.
     *
     * @param filmId идентификатор фильма.
     * @param count  количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    Collection<Review> getFilmReviews(long filmId, int count);

    /**
     * Получить отзыв по его идентификатору.
     *
     * @param reviewId идентификатор отзыва.
     * @return отзыв.
     */
    Optional<Review> getReviewById(long reviewId);

    /**
     * Обновить отзыв.
     *
     * @param review отзыв.
     */
    void updateReview(Review review);

    /**
     * Удалить отзыв.
     *
     * @param reviewId идентификатор отзыва.
     */
    void deleteReview(long reviewId);

    //endregion

    //region Лайки

    /**
     * Добавить лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     * @param isLiked  признак, нравится ли пользователю отзыв.
     */
    void addLikeToReview(long reviewId, long userId, boolean isLiked);

    /**
     * Проверить существует ли у отзыва лайк от пользователя.
     * @param reviewId идентификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    boolean isLikeForReviewExist(long reviewId, long userId);

    /**
     * Обновить лайк у отзыва.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     * @param isLiked  признак, нравится ли пользователю отзыв.
     */
    void updateLikeForReview(long reviewId, long userId, boolean isLiked);

    /**
     * Удалить лайк у отзыва.
     *
     * @param reviewId  идентификатор отзыва.
     * @param userId    идентификатор пользователя.
     */
    void removeLikeFromReview(long reviewId, long userId);

    //endregion
}
