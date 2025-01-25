package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.BooleanMapper;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.dao.queries.ReviewQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewRepository extends BaseRepository implements ReviewStorage {

    //region Отзывы

    /**
     * Создать новый отзыв.
     *
     * @param review отзыв.
     * @return отзыв.
     */
    @Override
    public Review createReview(Review review) {
        try {
            long id = this.insert(ReviewQueries.CREATE_REVIEW_QUERY, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());
            review.setId(id);

            log.debug("Отзыв от пользователя с id = {} к фильму с id = {} успешно добавлен с id = {}", review.getUserId(), review.getFilmId(), id);
            return review;
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении отзыва от пользователя с id = {} к фильму с id = {}: [{}] {}", review.getUserId(), review.getFilmId(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список отзывов.
     *
     * @param count количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    @Override
    public Collection<Review> getReviews(int count) {
        try {
            return this.findMany(ReviewQueries.GET_REVIEWS_QUERY, ReviewMapper::mapToReview, count);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка всех отзывов: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список отзывов к фильму.
     *
     * @param filmId идентификатор фильма.
     * @param count  количество отзывов, которое необходимо получить.
     * @return список отзывов.
     */
    @Override
    public Collection<Review> getFilmReviews(long filmId, int count) {
        try {
            return this.findMany(ReviewQueries.GET_REVIEWS_QUERY.replace("1 = 1", "r.film_id = ?"), ReviewMapper::mapToReview, filmId, count);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка отзывов для фильма с id = {}: [{}] {}", filmId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить отзыв по его идентификатору.
     *
     * @param reviewId идентификатор отзыва.
     * @return отзыв.
     */
    @Override
    public Optional<Review> getReviewById(long reviewId) {
        try {
            return this.findOne(ReviewQueries.GET_REVIEW_BY_ID_QUERY, ReviewMapper::mapToReview, reviewId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении отзыва с id = {}: [{}] {}", reviewId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Обновить отзыв.
     *
     * @param review отзыв.
     */
    @Override
    public void updateReview(Review review) {
        try {
            this.update(ReviewQueries.UPDATE_REVIEW_QUERY, review.getContent(), review.getIsPositive(), review.getId());
        } catch (Throwable ex) {
            log.error("Ошибка при обновлении отзыва с id = {}: [{}] {}", review.getId(), ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить отзыв.
     *
     * @param reviewId идентификатор отзыва.
     */
    @Override
    public void deleteReview(long reviewId) {
        try {
            this.delete(ReviewQueries.DELETE_REVIEW_QUERY, reviewId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении отзыва с id = {}: [{}] {}", reviewId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
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
    @Override
    public void addLikeToReview(long reviewId, long userId, boolean isLiked) {
        try {
            this.insert(ReviewQueries.ADD_LIKE_TO_REVIEW_QUERY, reviewId, userId, isLiked);
        } catch (Throwable ex) {
            log.error("Ошибка при добавления лайка к отзыву с id = {} от пользователя с id = {}: [{}] {}", reviewId, userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Проверить существует ли у отзыва лайк от пользователя.
     * @param reviewId идентификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    @Override
    public boolean isLikeForReviewExist(long reviewId, long userId) {
        try {
            return this.findOne(ReviewQueries.IS_LIKE_FOR_REVIEW_EXISTS_QUERY, BooleanMapper::mapToBoolean, reviewId, userId).orElse(false);
        } catch (Throwable ex) {
            log.error("Ошибка при проверке существования лайка к отзыву с id = {} от пользователя с id = {}: [{}] {}", reviewId, userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Обновить лайк у отзыва.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     * @param isLiked  признак, нравится ли пользователю отзыв.
     */
    @Override
    public void updateLikeForReview(long reviewId, long userId, boolean isLiked) {
        try {
            this.update(ReviewQueries.UPDATE_LIKE_FOR_REVIEW_QUERY, isLiked, reviewId, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при обновлении лайка у отзыва с id = {} от пользователя с id = {}: [{}] {}", reviewId, userId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Удалить лайк у отзыва.
     *
     * @param reviewId идентификатор отзыва.
     * @param userId   идентификатор пользователя.
     */
    @Override
    public void removeLikeFromReview(long reviewId, long userId) {
        try {
            this.delete(ReviewQueries.REMOVE_LIKE_FROM_REVIEW_QUERY, reviewId, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при удалении лайка пользователя с id = {} у отзыва с id = {}: [{}] {}", userId, reviewId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    //endregion
}
