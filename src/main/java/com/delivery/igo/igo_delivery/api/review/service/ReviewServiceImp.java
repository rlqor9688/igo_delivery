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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReviewServiceImp implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponseDto createReview(Authuser authuser, ReviewRequestDto requestDto) {
        Users findUser = userRepository.findById(authuser.getId());
        Orders findOrder = orderRepository.findById(requestDto.getOrdersId())
                .orElseThrow(() -> new GlobalException(ORDER_NOT_FOUND));
        Stores findStore = storeRepository.findById(requestDto.getStoresId())
                .orElseThrow(() -> new GlobalException(STORE_NOT_FOUND));

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
