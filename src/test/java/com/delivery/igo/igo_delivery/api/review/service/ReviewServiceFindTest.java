package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.review.entity.ReviewStatus;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import com.delivery.igo.igo_delivery.api.review.repository.ReviewRepository;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceFindTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Users user;

    private Reviews review;

    private Stores store;

    @BeforeEach
    void setUp() {

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

        store = Stores.builder()
                .id(1L)
                .users(user)
                .storeName("가게")
                .build();
    }

    @Test
    void 리뷰_조회에_성공한다() {
        // given
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(reviewRepository.findAllByStores_IdAndRatingBetween(store.getId(), 1, 5)).willReturn(List.of(review));

        // when
        reviewService.findAllReviewByStore(store.getId(), 1, 5);

        // then
        verify(storeRepository).findById(store.getId());
        verify(reviewRepository).findAllByStores_IdAndRatingBetween(review.getId(), 1, 5);
    }

    @Test
    void store가_존재하지_않으면_STORE_NOT_FOUND_예외를_던진다() {
        // given
        given(storeRepository.findById(store.getId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.findAllReviewByStore(store.getId(),1,5));

        // then
        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void store가_폐업상태면_REVIEW_STORE_IS_CLOSED_예외를_던진다() {
        // given
        Stores store = Stores.builder()
                .id(1L)
                .users(user)
                .storeName("가게")
                .storeStatus(StoreStatus.CLOSED)
                .build();
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.findAllReviewByStore(store.getId(),1,5));

        // then
        assertEquals(ErrorCode.REVIEW_STORE_IS_CLOSED, exception.getErrorCode());
    }
}
