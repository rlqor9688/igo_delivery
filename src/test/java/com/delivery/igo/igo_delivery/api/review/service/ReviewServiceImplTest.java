package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
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

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private AuthUser authUser;

    private Users user;

    private Orders order;

    private OrderItems orderItem;

    private Stores store;

    private Menus menu;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "test@gmail.com", "nickname", UserRole.CONSUMER);

        user = Users.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build();

        order = Orders.builder()
                .id(1L)
                .users(user)
                .orderStatus(OrderStatus.COMPLETE)
                .orderAddress("부산시")
                .build();

        store = Stores.builder()
                .id(1L)
                .users(user)
                .storeName("가게")
                .build();

        menu = Menus.builder()
                .id(1L)
                .stores(store)
                .menuName("메뉴이름")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .build();

        orderItem = OrderItems.builder()
                .id(1L)
                .orders(order)
                .menus(menu)
                .orderItemPrice(1000L)
                .orderQuantity(10)
                .build();

    }

    @Test
    void createReview_리뷰_생성에_성공한다() {
        //given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "컨텐츠", 5);
        Reviews review = Reviews.of(user, order, store, requestDto);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));
        given(orderItemsRepository.findByOrdersId(requestDto.getOrdersId())).willReturn(List.of(orderItem));
        given(reviewRepository.save(any())).willReturn(review);

        //when
        ReviewResponseDto responseDto = reviewService.createReview(authUser, requestDto);

        assertNotNull(responseDto);

        //then
        verify(userRepository).findById(authUser.getId());
        verify(orderRepository).findById(requestDto.getOrdersId());
        verify(storeRepository).findById(requestDto.getStoresId());
        verify(orderItemsRepository).findByOrdersId(requestDto.getOrdersId());
        verify(reviewRepository).save(any(Reviews.class));
    }

    @Test
    void createReview_authUser가_null이면_USER_NOT_FOUND_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰내용", 5);

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(null, requestDto));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReview_유저가_없으면_USER_NOT_FOUND_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L,1L,"리뷰내용",5);
        given(userRepository.findById(authUser.getId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.createReview(authUser, requestDto)
        );

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReview_주문이_없으면_ORDER_NOT_FOUND_에러를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L,1L,"리뷰내용",5);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.empty()); // 주문 없음 처리

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.createReview(authUser,requestDto)
        );

        // then
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReview_가게가_없으면_STORE_NOT_FOUND_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰내용", 5);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order)); // 주문 없음 처리
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReview_유저상태가_INACTIVE면_DELETED_USER_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        Users deletedUser = Users.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.INACTIVE)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(deletedUser));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());
    }

    @Test
    void createReview_유저권한이_CONSUMER가_아니면_ROLE_CONSUMER_FORBIDDEN_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        Users owner = Users.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(owner));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.ROLE_CONSUMER_FORBIDDEN, exception.getErrorCode());
    }

    @Test
    void createReview_본인주문이_아니면_FORBIDDEN_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        Users anotherUser = Users.builder()
                .id(2L)// authUser와 다른 ID
                .email("hacker@naver.com")
                .nickname("침입자")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build();

        Orders orderByAnotherUser = Orders.builder()
                .id(1L)
                .users(anotherUser)
                .orderStatus(OrderStatus.COMPLETE)
                .orderAddress("서울시")
                .build();


        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(orderByAnotherUser));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));

        // when
        GlobalException exception = assertThrows(GlobalException.class, ()->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }

    @Test
    void createReview_주문상태가_COMPLETE가_아니면_REVIEW_ORDER_INVALID_예외를_던진다(){
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        Orders incompleteOrder = Orders.builder()
                .id(1L)
                .users(user)
                .orderStatus(OrderStatus.CANCELLED)
                .orderAddress("부산시")
                .build();
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(incompleteOrder));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(authUser, requestDto)
        );

        // then
        assertEquals(ErrorCode.REVIEW_ORDER_INVALID, exception.getErrorCode());
    }

    @Test
    void createReview_주문상품이_없으면_REVIEW_ORDERITEM_NOT_FOUND_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));
        given(orderItemsRepository.findByOrdersId(requestDto.getOrdersId())).willReturn(List.of()); // 빈 리스트 반환

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.REVIEW_ORDERITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createReview_주문과_가게가_매칭되지_않으면_REVIEW_STORE_MISMATCH_예외를_던진다() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "리뷰 내용", 5);
        Stores anotherStore = Stores.builder()
                .id(2L) // 가게 번호를 requestDto와 다르게 설정
                .users(user)
                .storeName("가게")
                .build();

        Menus anotherMenu = Menus.builder()
                .id(1L)
                .stores(anotherStore)
                .menuName("메뉴이름")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .build();

        OrderItems anotherOrderItem = OrderItems.builder()
                .id(1L)
                .orders(order)
                .menus(anotherMenu)
                .orderItemPrice(10000L)
                .orderQuantity(1)
                .build();

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(requestDto.getOrdersId())).willReturn(Optional.of(order));
        given(storeRepository.findById(requestDto.getStoresId())).willReturn(Optional.of(store));
        given(orderItemsRepository.findByOrdersId(requestDto.getOrdersId())).willReturn(List.of(anotherOrderItem)); //

        // when
        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(authUser, requestDto));

        // then
        assertEquals(ErrorCode.REVIEW_STORE_MISMATCH, exception.getErrorCode());
    }
}
