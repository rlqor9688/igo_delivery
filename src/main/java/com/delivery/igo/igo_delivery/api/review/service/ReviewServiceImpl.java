package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import com.delivery.igo.igo_delivery.api.order.repository.OrderItemsRepository;
import com.delivery.igo.igo_delivery.api.order.repository.OrderRepository;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import com.delivery.igo.igo_delivery.api.review.repository.ReviewRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final OrderRepository orderRepository;

    private final OrderItemsRepository orderItemsRepository;

    private final StoreRepository storeRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponseDto createReview(AuthUser authUser, ReviewRequestDto requestDto) {
        // 로그인한 유저가 고객(CONSUMER)인지 확인
        if (!Objects.equals(authUser.getUserRole(), UserRole.CONSUMER)) {
            throw new GlobalException(ErrorCode.INVALID_USER_ROLE);
        }

        // 유저, 주문, 가게 정보 DB에서 찾기
        Users findUser = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        Orders findOrder = orderRepository.findById(requestDto.getOrdersId())
                .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_NOT_FOUND));

        Stores findStore = storeRepository.findById(requestDto.getStoresId())
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        // 주문의 usersId와 authUser의 usersId 가 같은지 검증 (본인이 남긴 주문에 리뷰를 남기는 상황인지 검증)
        if (!Objects.equals(authUser.getId(), findOrder.getUsers().getId())) {
            throw new GlobalException(ErrorCode.REVIEW_USER_MISMATCH);
        }

        /**
         * 주문에 연결된 매장id와 dto로 입력한 매장id가 일치하는지 확인
         * <Process>
         *     - ordersId로 orderItemsRepository에서 orderItem을 조회
         *     - 조회한 orderItem의 menu에서 stores를 조회하고, stores의 id를 조회
         *     - menusId로 storesId를 찾았을 때, dto에서 입력한 storesId와 같은지 검증
         * </Process>
         */
        List<OrderItems> orderItemsList = orderItemsRepository.findByOrdersId(requestDto.getOrdersId());
        if (!orderItemsList.isEmpty()) {
            OrderItems findOrderItem = orderItemsList.get(0);
            if (!Objects.equals(findOrderItem.getMenus().getStores().getId(),findStore.getId())) {
                throw new GlobalException(ErrorCode.REVIEW_STORE_MISMATCH);
            }
        } else {
            throw new GlobalException(ErrorCode.REVIEW_ORDERITEM_NOT_FOUND);
        }

        // 리뷰 생성
        Reviews review = Reviews.of(findUser, findOrder, findStore, requestDto);

        // 생성한 리뷰 저장
        Reviews savedReview = reviewRepository.save(review);

        // 생성한 리뷰 Dto로 반환
        return ReviewResponseDto.of(savedReview);
    }
}