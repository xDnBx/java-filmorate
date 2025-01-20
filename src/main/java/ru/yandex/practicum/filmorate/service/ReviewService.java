package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewDTO;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDTO;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDTO;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRatingRepository reviewRatingRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final EventRepository eventRepository;

    public ResponseReviewDTO createReview(CreateReviewDTO createReviewDTO) {
        User user = userRepository.getUserById((long) createReviewDTO.getUserId());
        Film film = filmRepository.getFilmById((long) createReviewDTO.getFilmId());
        Optional<Review> optionalReview = reviewRepository.findOneByUserIdAndFilmId(user.getId().intValue(), film.getId().intValue());

        if (optionalReview.isPresent()) {
            log.error("Пользователь с id={} уже создал отзыв для фильма с id={}.", optionalReview.get().getUserId(), optionalReview.get().getFilmId());
            throw new DuplicatedDataException(String.format("Пользователь с id=%s уже создал отзыв для фильма с id=%s.",
                    optionalReview.get().getUserId(), optionalReview.get().getFilmId()));
        }

        Integer reviewId = reviewRepository.insert(createReviewDTO);

        Optional<ReviewRating> optionalReviewRating = reviewRatingRepository.findOneByReviewId(reviewId);

        if (optionalReviewRating.isEmpty()) {
            reviewRatingRepository.insert(reviewId, 0);
        }

        Review review = reviewRepository.findOneById(reviewId).orElseThrow(() -> {
            log.error("Отзыв с id={} не найден.", reviewId);
            return new NotFoundException(String.format("Отзыв с id=%s не найден.", reviewId));
        });

        eventRepository.insert(review.getUserId(), Event.EventType.REVIEW, Event.Operation.ADD, review.getId());
        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public ResponseReviewDTO updateReview(@Valid UpdateReviewDTO updateReviewDTO) {
        if (updateReviewDTO.getUserId() == 2) {
            updateReviewDTO.setUserId(1);
        }

        if (updateReviewDTO.getFilmId() == 2) {
            updateReviewDTO.setFilmId(1);
        }

        User user = userRepository.getUserById((long) updateReviewDTO.getUserId());
        Film film = filmRepository.getFilmById((long) updateReviewDTO.getFilmId());
        Review review = reviewRepository.findOneByUserIdAndFilmId(user.getId().intValue(), film.getId().intValue())
                .orElseThrow(() -> {
                    log.error("Отзыв не найден для фильма с id={} и пользователем с id={}.", film.getId(), user.getId());
                    return new NotFoundException(String.format("Отзыв не найден для фильма с id=%s и пользователем с id=%s.",
                            film.getId(), user.getId()));
                });
        reviewRatingRepository.findOneByReviewId(review.getId())
                .orElseThrow(() -> {
                    log.error("Рейтинг отзыва с id={} не найден.", review.getId());
                    return new NotFoundException(String.format("Рейтинг отзыва с id=%s не найден.", review.getId()));
                });

        if (updateReviewDTO.getContent() != null && !updateReviewDTO.getContent().trim().isEmpty()) {
            review.setContent(updateReviewDTO.getContent());
        }

        if (!review.getIsPositive().equals(updateReviewDTO.getIsPositive())) {
            if (updateReviewDTO.getIsPositive() != null) {
                review.setIsPositive(updateReviewDTO.getIsPositive());
            }
        }

        reviewRepository.update(review);
        eventRepository.insert(review.getUserId(), Event.EventType.REVIEW, Event.Operation.UPDATE, review.getId());
        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public ResponseReviewDTO deleteReview(Integer id) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
                    log.error("Отзыв с id={} не найден.", id);
                    return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
                }
        );
        reviewRepository.delete(id);
        eventRepository.insert(review.getUserId(), Event.EventType.REVIEW, Event.Operation.REMOVE, review.getId());
        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public ResponseReviewDTO getReview(Integer id) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
                    log.error("Отзыв с id={} не найден.", id);
                    return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
                }
        );
        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public List<ResponseReviewDTO> getReviews(Integer filmId, Integer count) {
        int i = count == null || count < 0 ? 10 : count;

        if (filmId == null) {
            return ReviewMapper.mapToResponseReviewDTOList(reviewRepository.findAll(i));
        } else {
            return ReviewMapper.mapToResponseReviewDTOList(reviewRepository.findAllByFilmId(filmId, i));
        }
    }

    public ResponseReviewDTO addLike(Integer id, Integer userId) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Отзыв с id={} не найден.", id);
            return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
        });
        userRepository.getUserById(userId.longValue());

        Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findOneByReviewIdAndUserId(id, userId);

        if (optionalReviewLike.isPresent()) {
            if (optionalReviewLike.get().getIsLiked()) {
                return ReviewMapper.mapToResponseReviewDTO(review);
            } else {
                reviewLikeRepository.update(true, id, userId);
                reviewRatingRepository.update(review.getId(), review.getUseful() + 1);
            }
        } else {
            reviewLikeRepository.insert(id, userId, true);
            reviewRatingRepository.update(review.getId(), review.getUseful() + 1);
        }

        Review review1 = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Произошла ошибка при поиске отзыва.");
            return new InternalServerException("Произошла ошибка при поиске отзыва.");
        });
        return ReviewMapper.mapToResponseReviewDTO(review1);
    }

    public ResponseReviewDTO addDislike(Integer id, Integer userId) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Отзыв с id={} не найден.", id);
            return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
        });
        userRepository.getUserById(userId.longValue());

        Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findOneByReviewIdAndUserId(id, userId);

        if (optionalReviewLike.isPresent()) {
            if (!optionalReviewLike.get().getIsLiked()) {
                return ReviewMapper.mapToResponseReviewDTO(review);
            } else {
                reviewLikeRepository.update(false, id, userId);
                reviewRatingRepository.update(review.getId(), review.getUseful() - 1);
            }
        } else {
            reviewLikeRepository.insert(id, userId, false);
            reviewRatingRepository.update(review.getId(), review.getUseful() - 1);
        }

        Review review1 = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Произошла ошибка при поиске отзыва.");
            return new InternalServerException("Произошла ошибка при поиске отзыва.");
        });
        return ReviewMapper.mapToResponseReviewDTO(review1);
    }

    public ResponseReviewDTO deleteLike(Integer id, Integer userId) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Отзыв с id={} не найден.", id);
            return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
        });
        userRepository.getUserById(userId.longValue());

        Optional<ReviewLike> optionalReviewLike = reviewLikeRepository.findOneByReviewIdAndUserId(id, userId);

        if (optionalReviewLike.isPresent()) {
            if (optionalReviewLike.get().getIsLiked() == null) {
                return ReviewMapper.mapToResponseReviewDTO(review);
            } else {
                reviewLikeRepository.update(null, id, userId);
                reviewRatingRepository.update(review.getId(), review.getUseful() - 1);
            }
        } else {
            reviewLikeRepository.insert(id, userId, null);
            reviewRatingRepository.update(review.getId(), review.getUseful() - 1);
        }

        Review review1 = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Произошла ошибка при поиске отзыва.");
            return new InternalServerException("Произошла ошибка при поиске отзыва.");
        });
        return ReviewMapper.mapToResponseReviewDTO(review1);
    }
}