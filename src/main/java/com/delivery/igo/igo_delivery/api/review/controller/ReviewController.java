package com.delivery.igo.igo_delivery.api.review.controller;

import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewUpdateRequestDto;
import com.delivery.igo.igo_delivery.api.review.service.ReviewService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @Auth AuthUser authUser,
            ReviewRequestDto requestDto)
    {
        return new ResponseEntity<>(reviewService.createReview(authUser, requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @Auth AuthUser authUser,
            ReviewUpdateRequestDto requestDto
    ) {
        reviewService.updateReview(reviewId, authUser, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> findAllReviewByStore(
            @RequestParam Long storesId,
            @RequestParam(defaultValue = "1") int minRating,
            @RequestParam(defaultValue = "5") int maxRating) {
        return new ResponseEntity<>(reviewService.findAllReviewByStore(storesId, minRating, maxRating), HttpStatus.OK);
    }

    @DeleteMapping("/{reviewsId}")
    public ResponseEntity<Void> deleteReview(@Auth AuthUser authUser, @PathVariable Long reviewsId) {
        reviewService.deleteReview(authUser, reviewsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
