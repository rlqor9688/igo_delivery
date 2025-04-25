package com.delivery.igo.igo_delivery.api.review.service;

import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
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
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
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

    @Mock
    private MenuRepository menuRepository;

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
                .build();

        order = Orders.builder()
                .id(1L)
                .users(user)
                .orderStatus(OrderStatus.CANCELLED)
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
    void reviews_리뷰_생성에_성공한다() {
        //given
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, 1L, "컨텐츠", 5);
        Reviews review = Reviews.of(user, order, store, requestDto);
        Long storeId = 1L;

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(orderItemsRepository.findByOrdersId(requestDto.getOrdersId())).willReturn(List.of(orderItem));
        given(reviewRepository.save(any())).willReturn(review);

        //when
        ReviewResponseDto responseDto = reviewService.createReview(authUser, requestDto);

        assertNotNull(responseDto);

        //then
        verify(userRepository).findById(authUser.getId());
        verify(orderRepository).findById(order.getId());
        verify(storeRepository).findById(storeId);
        verify(orderItemsRepository).findByOrdersId(order.getId());
        verify(reviewRepository).save(any(Reviews.class));
    }



}
