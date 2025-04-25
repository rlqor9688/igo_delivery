package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.review.dto.ReviewUpdateRequestDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceUpdateTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private AuthUser authUser;

    private Users user;

    private Reviews review;

    @BeforeEach
    public void setUp() {
        authUser = new AuthUser(1L, "test@gmail.com", "nickname", UserRole.CONSUMER);

        user = Users.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build();

        review = Reviews.builder()
                .id(1L)
                .users(user)
                .content("리뷰내용")
                .rating(5)
                .reviewStatus(ReviewStatus.LIVE)
                .build();
    }

    @Test
    void updateReview_리뷰_업데이트에_성공한다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 리뷰", 4);
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));

        // when
        reviewService.updateReview(review.getId(), authUser, requestDto);

        // then
        //내부 값이 실제로 변경 되었는지 확인
        assertEquals("수정 리뷰",  review.getContent());
        assertEquals(4, review.getRating());

        // 호출 여부 확인
        verify(reviewRepository).findById(review.getId());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void updateReview_authUser가_Null이면_USER_NOT_FOUND_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 리뷰", 4);

        //when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.updateReview(1L, null, requestDto));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReview_review가_존재하지_않으면_REVIEW_NOT_FOUND_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 내용", 4);
        given(reviewRepository.findById(review.getId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.updateReview(review.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReview_review가_삭제상태면_REVIEW_IS_DELETED_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 내용", 4);
        Reviews deletedReview = Reviews.builder()
                .id(1L)
                .users(user)
                .reviewStatus(ReviewStatus.DELETED)
                .build();
        given(reviewRepository.findById(deletedReview.getId())).willReturn(Optional.of(deletedReview));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.updateReview(deletedReview.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.REVIEW_IS_DELETED, exception.getErrorCode());
    }

    @Test
    void updateReview_reviewId와_authUser의_userId가_다르면_FORBIDDEN_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 내용", 4);

        Users anotherUser = Users.builder()
                .id(2L)
                .build();
        Reviews anotherReview = Reviews.builder()
                .id(2L)
                .users(anotherUser)
                .reviewStatus(ReviewStatus.LIVE)
                .build();

        given(reviewRepository.findById(anotherReview.getId())).willReturn(Optional.of(anotherReview));

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.updateReview(anotherReview.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }

    @Test
    void updateReview_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 내용", 3);
        given(userRepository.findById(authUser.getId())).willReturn(Optional.empty());
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.updateReview(review.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateReview_유저상태가_INACTIVE면_DELETED_USER_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 리뷰", 3);
        Users user = Users.builder()
                .id(1L)
                .userStatus(UserStatus.INACTIVE)
                .userRole(UserRole.CONSUMER)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.updateReview(review.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());
    }

    @Test
    void updateReview_유저권한이_CONSUMER가_아니면_ROLE_CONSUMER_FORBIDDEN_예외를_던진다() {
        // given
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정 내용", 2);
        Users user = Users.builder()
                .id(1L)
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.updateReview(review.getId(), authUser, requestDto));

        // then
        assertEquals(ErrorCode.ROLE_CONSUMER_FORBIDDEN, exception.getErrorCode());
    }
}
