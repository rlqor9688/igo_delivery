package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import com.delivery.igo.igo_delivery.api.order.repository.OrderItemsRepository;
import com.delivery.igo.igo_delivery.api.order.repository.OrderRepository;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewRequestDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewResponseDto;
import com.delivery.igo.igo_delivery.api.review.dto.ReviewUpdateRequestDto;
import com.delivery.igo.igo_delivery.api.review.entity.ReviewStatus;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import com.delivery.igo.igo_delivery.api.review.repository.ReviewRepository;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        // authUser NPE 방지
        if (authUser == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // DB에서 유저 조회 + 유효성 검증(UserStatus= LIVE, UserRole = CONSUMER)
        Users findUser = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        validateUserDeletionAndRole(findUser);

        // DB에서 주문 조회 + 본인 확인 + 주문 상태 검증
        Orders findOrder = orderRepository.findById(requestDto.getOrdersId())
                .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_NOT_FOUND));
        findOrder.getUsers().validateAccess(authUser.getId());// 주문의 usersId와 authUser의 usersId 가 같은지 검증 (본인이 남긴 주문에 리뷰를 남기는 상황인지 검증)
        if (!Objects.equals(findOrder.getOrderStatus(), OrderStatus.COMPLETE)) { // 주문 완료인 경우에만 리뷰를 남길 수 있음
            throw new GlobalException(ErrorCode.REVIEW_ORDER_INVALID);
        }

        // 매장 조회
        Stores findStore = storeRepository.findById(requestDto.getStoresId())
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        /**
         * 주문에 연결된 매장id와 dto로 입력한 매장id가 일치하는지 확인
         * <Process>
         *     - ordersId로 orderItemsRepository에서 orderItem을 조회
         *     - 조회한 orderItem의 menu에서 stores를 조회하고, stores의 id를 조회
         *     - menusId로 storesId를 찾았을 때, dto에서 입력한 storesId와 같은지 검증
         * </Process>
         */
        // 주문 상품 존재 및 storeId 일치 검증
        List<OrderItems> orderItemsList = orderItemsRepository.findByOrdersId(requestDto.getOrdersId());
        if (orderItemsList.isEmpty()) {
            throw new GlobalException(ErrorCode.REVIEW_ORDERITEM_NOT_FOUND);
        }
        OrderItems findOrderItem = orderItemsList.get(0);
        if (!Objects.equals(findOrderItem.getMenus().getStores().getId(),findStore.getId())) {
            throw new GlobalException(ErrorCode.REVIEW_STORE_MISMATCH);
        }

        // 리뷰 생성 및 저장
        Reviews review = Reviews.of(findUser, findOrder, findStore, requestDto);
        Reviews savedReview = reviewRepository.save(review);
        return ReviewResponseDto.from(savedReview);
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, AuthUser authUser, ReviewUpdateRequestDto requestDto) {
        // authUser NPE 방지
        if (authUser == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // 로그인 유저 DB 조회 + 상태(LIVE), 권한(CONSUMER) 확인
        Users findUser = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        validateUserDeletionAndRole(findUser);

        // 입력받은 Review DB 존재/ 활성화 여부 조회 + 로그인 유저 일치 여부 확인
        Reviews findReview = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new GlobalException(ErrorCode.REVIEW_NOT_FOUND));
        if (!Objects.equals(findReview.getReviewStatus(), ReviewStatus.LIVE)) {
            throw new GlobalException(ErrorCode.REVIEW_IS_DELETED);
        }
        validateReviewAccess(findReview, authUser);

        // 리뷰 수정 및 저장
        findReview.update(requestDto.getContent(), requestDto.getRating());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> findAllReviewByStore(Long storesId, int minRating, int maxRating) {
        // 매장 조회 + 매장 활성화 여부 조회
        Stores findStore = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));
        if (Objects.equals(findStore.getStoreStatus(), StoreStatus.CLOSED)) {
            throw new GlobalException(ErrorCode.REVIEW_STORE_IS_CLOSED);
        }

        List<Reviews> reviewList = reviewRepository.findAllByStoresIdAndRatingRange(storesId, minRating, maxRating);

        return reviewList.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 리뷰 수정 권한 검증(작성자=로그인유저)
    private void validateReviewAccess(Reviews review, AuthUser authUser) {
        review.getUsers().validateAccess(authUser.getId());
    }

    // 유저 유효성 검증(UserStatus=LIVE, UserRole = CONSUMER)
    private void validateUserDeletionAndRole(Users user) {
        user.validateDelete();
        user.validateConsumer();
    }
}