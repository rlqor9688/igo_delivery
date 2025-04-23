package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.cart.repository.OrderRepository;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import com.delivery.igo.igo_delivery.api.review.repository.ReviewRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.delivery.igo.igo_delivery.common.exception.ErrorCode.ORDER_NOT_FOUND;
import static com.delivery.igo.igo_delivery.common.exception.ErrorCode.STORE_NOT_FOUND;

@Service
@Slf4j
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto createReview(AuthUser authUser, ReviewRequestDto requestDto) {
        // 유저, 주문, 가게 정보 DB에서 찾기
        Users findUser = userRepository.findById(authUser.getId());
        Orders findOrder = orderRepository.findById(requestDto.getOrdersId())
                .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_NOT_FOUND));
        Stores findStore = storeRepository.findById(requestDto.getStoresId())
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        // 리뷰 생성
        Reviews review = new Reviews(
                findUser,
                findOrder,
                findStore,
                requestDto.getContent(),
                requestDto.getRating()
        );

        // 생성한 리뷰 저장
        Reviews savedReview = reviewRepository.save(review);

        // 생성한 리뷰 Dto로 반환
        return new ReviewResponseDto(
                savedReview.getUsers().getId(),
                savedReview.getRating(),
                savedReview.getContent(),
                savedReview.getCreatedAt()
        );
    }
}