package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(AuthUser authUser, ReviewRequestDto reviewRequestDto);
//    void updateReview(Long reviewId, AuthUser authUser, ReviewRequestDto reviewRequestDto);
//    List<ReviewResponseDto> findAllReviewByStore(Long storeId);
//    void deleteReview(Long reviewId, AuthUser authUser);
}
