package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.review.entity.ReviewStatus;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import com.delivery.igo.igo_delivery.api.review.repository.ReviewRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceDeleteTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private AuthUser authUser;

    private Users user;

    private Reviews review;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "test@gmail.com", "nickname", UserRole.CONSUMER);

        user = Users.builder()
                .id(1L)
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build();

        review = Reviews.builder()
                .id(1L)
                .users(user)
                .reviewStatus(ReviewStatus.LIVE)
                .build();
    }

    @Test
    void deleteReview_리뷰_삭제에_성공한다() {
        // given
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));

        // when
        reviewService.deleteReview(authUser, review.getId());

        // then
        assertEquals(ReviewStatus.DELETED,  review.getReviewStatus());

        // 호출 여부 확인
        verify(reviewRepository).findById(review.getId());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void deleteReview_authUser가_Null이면_USER_NOT_FOUND_예외를_던진다(){
        // given & when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.deleteReview(null, review.getId()));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReview_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        // given
        given(userRepository.findById(authUser.getId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.deleteReview(authUser, review.getId()));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReview_유저상태가_INACTIVE면_DELETED_USER_예외를_던진다() {
        // given
        user = Users.builder()
                .id(1L)
                .userStatus(UserStatus.INACTIVE)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.deleteReview(authUser, review.getId()));

        // then
        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());
    }

    @Test
    void deleteReview_유저권한이_CONSUMER가_아니면_ROLE_CONSUMER_FORBIDDEN_예외를_던진다() {
        // given
        user = Users.builder()
                .id(1L)
                .userStatus(UserStatus.LIVE)
                .userRole(UserRole.OWNER)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.deleteReview(authUser, review.getId()));

        // then
        assertEquals(ErrorCode.ROLE_CONSUMER_FORBIDDEN, exception.getErrorCode());
    }

    @Test
    void deleteReview_review가_존재하지_않으면_REVIEW_NOT_FOUND_예외를_던진다() {
        // given
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.deleteReview(authUser, review.getId()));

        // then
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteReview_review가_삭제상태면_REVIEW_ALREADY_DELETED_예외를_던진다() {
        // given
        review = Reviews.builder()
                .id(1L)
                .reviewStatus(ReviewStatus.DELETED)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.deleteReview(authUser, review.getId()));

        // then
        assertEquals(ErrorCode.REVIEW_ALREADY_DELETED, exception.getErrorCode());
    }

    @Test
    void deleteReview_reviewId와_authUser의_userId가_다르면_FORBIDDEN_예외를_던진다() {
        // given
        Users anotherUser = Users.builder()
                .id(2L)
                .userStatus(UserStatus.LIVE)
                .userRole(UserRole.CONSUMER)
                .build();
        Reviews anotherReview = Reviews.builder()
                .id(2L)
                .users(anotherUser)
                .reviewStatus(ReviewStatus.LIVE)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findById(anotherReview.getId())).willReturn(Optional.of(anotherReview));

        // when
        GlobalException exception = assertThrows(GlobalException.class , () ->
                reviewService.deleteReview(authUser, anotherReview.getId()));

        // then
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }
}
