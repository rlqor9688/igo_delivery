package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewUpdateRequestDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(AuthUser authUser, ReviewRequestDto reviewRequestDto);
    void updateReview(Long reviewId, AuthUser authUser, ReviewUpdateRequestDto requestDto);
    List<ReviewResponseDto> findAllReviewByStore(Long storeId, int minRating, int maxRating);
    void deleteReview(AuthUser authUser, Long reviewId);
}
