package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewDTO;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDTO;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDTO;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseReviewDTO createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO) {
        return reviewService.createReview(createReviewDTO);
    }

    @PutMapping
    public ResponseReviewDTO updateReview(@Valid @RequestBody UpdateReviewDTO updateReviewDTO) {
        return reviewService.updateReview(updateReviewDTO);
    }

    @DeleteMapping("{id}")
    public ResponseReviewDTO deleteReview(@PathVariable Integer id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("{id}")
    public ResponseReviewDTO getReview(@PathVariable Integer id) {
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<ResponseReviewDTO> getReviews(@RequestParam(required = false) Integer filmId,
                                              @RequestParam(required = false) Integer count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseReviewDTO addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public ResponseReviewDTO addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseReviewDTO deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public ResponseReviewDTO deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.deleteLike(id, userId);
    }
}
