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
    private final FilmRatingRepository filmRatingRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ResponseReviewDTO createReview(CreateReviewDTO createReviewDTO) {
        User user = userRepository.getUserById((long) createReviewDTO.getUserId());
        Film film = filmRepository.getFilmById((long) createReviewDTO.getFilmId());
        reviewRepository.findOneByUserIdAndFilmId(user.getId().intValue(), film.getId().intValue())
                .ifPresent(review -> {
                    log.error("Пользователь с id={} уже создал отзыв для фильма с id={}.", review.getUserId(), review.getFilmId());
                    throw new DuplicatedDataException(String.format("Пользователь с id=%s уже создал отзыв для фильма с id=%s.",
                            review.getUserId(), review.getFilmId()));
                });

        filmRatingRepository.insert(film.getId().intValue(), 0);
        Integer reviewId = reviewRepository.insert(createReviewDTO);
        Review review = reviewRepository.findOneById(reviewId).orElseThrow(() -> {
            log.error("Отзыв с id={} не найден.", reviewId);
            return new NotFoundException(String.format("Отзыв с id=%s не найден.", reviewId));
        });

        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public ResponseReviewDTO updateReview(@Valid UpdateReviewDTO updateReviewDTO) {
        User user = userRepository.getUserById((long) updateReviewDTO.getUserId());
        Film film = filmRepository.getFilmById((long) updateReviewDTO.getFilmId());
        Review review = reviewRepository.findOneByUserIdAndFilmId(user.getId().intValue(), film.getId().intValue())
                .orElseThrow(() -> {
                    log.error("Отзыв не найден для фильма с id={} и пользователем с id={}.", film.getId(), user.getId());
                    return new NotFoundException(String.format("Отзыв не найден для фильма с id=%s и пользователем с id=%s.",
                            film.getId(), user.getId()));
                });
        FilmRating filmRating = filmRatingRepository.findOneByFilmId(film.getId().intValue())
                .orElseThrow(() -> {
                    log.error("Рейтинг фильма не найден для фильма с id={}.", film.getId());
                    return new NotFoundException(String.format("Рейтинг фильма не найден для фильма с id=%s.", film.getId()));
                });

        if (updateReviewDTO.getContent() != null && !updateReviewDTO.getContent().trim().isEmpty()) {
            review.setContent(updateReviewDTO.getContent());
        }


        if (!review.getIsPositive().equals(updateReviewDTO.getIsPositive())) {
            if (updateReviewDTO.getIsPositive() != null) {
                if (updateReviewDTO.getIsPositive()) {
                    review.setIsPositive(true);
                    filmRatingRepository.update(filmRating.getFilmId(), filmRating.getRating() + 1);
                } else {
                    review.setIsPositive(false);
                    filmRatingRepository.update(filmRating.getFilmId(), filmRating.getRating() - 1);
                }
            }
        }

        reviewRepository.update(review);
        return ReviewMapper.mapToResponseReviewDTO(review);
    }

    public ResponseReviewDTO deleteReview(Integer id) {
        Review review = reviewRepository.findOneById(id).orElseThrow(() -> {
                    log.error("Отзыв с id={} не найден.", id);
                    return new NotFoundException(String.format("Отзыв с id=%s не найден.", id));
                }
        );

        boolean isReviewDeleted = reviewRepository.delete(id);
        boolean isFilmRatingDeleted = filmRatingRepository.delete(review.getFilmId());
        if (isReviewDeleted && isFilmRatingDeleted) {
            return ReviewMapper.mapToResponseReviewDTO(review);
        } else {
            log.error("Произошла ошибка при удалении отзыва.");
            throw new InternalServerException("Произошла ошибка при удалении отзыва.");
        }
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
                filmRatingRepository.update(review.getFilmId(), review.getUseful() + 1);
            }
        } else {
            reviewLikeRepository.insert(id, userId, true);
            filmRatingRepository.update(review.getFilmId(), review.getUseful() + 1);
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
                filmRatingRepository.update(review.getFilmId(), review.getUseful() - 2);
            }
        } else {
            reviewLikeRepository.insert(id, userId, false);
            filmRatingRepository.update(review.getFilmId(), review.getUseful() - 1);
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
                filmRatingRepository.update(review.getFilmId(), review.getUseful() - 1);
            }
        } else {
            reviewLikeRepository.insert(id, userId, null);
            filmRatingRepository.update(review.getFilmId(), review.getUseful() - 1);
        }

        Review review1 = reviewRepository.findOneById(id).orElseThrow(() -> {
            log.error("Произошла ошибка при поиске отзыва.");
            return new InternalServerException("Произошла ошибка при поиске отзыва.");
        });
        return ReviewMapper.mapToResponseReviewDTO(review1);
    }
}
