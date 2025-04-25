package com.delivery.igo.igo_delivery.api.order.service;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.order.dto.ChangeOrderStatusRequest;
import com.delivery.igo.igo_delivery.api.order.dto.ChangeOrderStatusResponse;
import com.delivery.igo.igo_delivery.api.order.dto.CreateOrderRequest;
import com.delivery.igo.igo_delivery.api.order.dto.OrderResponse;
import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import com.delivery.igo.igo_delivery.api.order.repository.OrderItemsRepository;
import com.delivery.igo.igo_delivery.api.order.repository.OrderRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@email.com", "테스트", UserRole.CONSUMER);
        CreateOrderRequest request = new CreateOrderRequest("서울시 마포구");

        LocalTime localOpenTime = LocalTime.of(0, 1);
        LocalTime localEndTime = LocalTime.of(23, 59);

        // LocalTime을 Time으로 변환
        Time openTime = Time.valueOf(localOpenTime);
        Time endTime = Time.valueOf(localEndTime);

        Users users = Users.builder()
                .id(userId)
                .email("test@email.com")
                .userRole(UserRole.CONSUMER)
                .nickname("테스트")
                .build();

        Stores store = Stores.builder().users(users).openTime(openTime).endTime(endTime).minOrderPrice(15000).build();

        Carts carts = Carts.builder()
                .users(users)
                .build();

        Menus menus = Menus.builder()
                .id(1L)
                .menuName("치킨")
                .price(15000L)
                .stores(store)
                .build();

        CartItems cartItem = CartItems.builder()
                .menus(menus)
                .carts(carts)
                .cartPrice(menus.getPrice())
                .cartQuantity(2)
                .build();

        Orders orders = new Orders(users, request.getOrderAddress());

        given(userRepository.findById(userId)).willReturn(Optional.of(users));
        given(cartRepository.findByUsersId(userId)).willReturn(carts);
        given(cartItemsRepository.findAllByCarts(carts)).willReturn(List.of(cartItem));
        given(orderRepository.save(any(Orders.class))).willReturn(orders);
        given(orderItemsRepository.saveAll(anyList())).willReturn(null); // void 리턴

        // when

        OrderResponse response = orderService.createOrder(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(request.getOrderAddress(), response.getOrderAddress());
        verify(orderRepository).save(any(Orders.class));
        verify(orderItemsRepository).saveAll(anyList());
    }

    @Test
    void 고객_주문_취소_성공() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        Users user = Users.builder().id(userId).build();
        Orders order = new Orders(user, "주소지");
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest("CANCELLED");

        AuthUser authUser = new AuthUser(userId, "email@gmail.com", "테스트닉네임", UserRole.CONSUMER);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        ChangeOrderStatusResponse response = orderService.changeOrderStatus(authUser, request, orderId);

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
    }


    @Test
    void 매장주인_주문_상태_변경_성공() {
        // given
        Long ownerId = 1L;
        Long orderId = 1L;

        Users owner = Users.builder().id(ownerId).build();
        Stores store = Stores.builder().users(owner).build();
        Menus menu = Menus.builder().stores(store).build();
        Orders order = new Orders(owner,"주소지");
        OrderItems item = OrderItems.builder().menus(menu).orders(order).build();

        AuthUser authUser = new AuthUser(ownerId, "email@gmail.com", "테스트닉네임", UserRole.OWNER);
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest("COOKING");

        given(userRepository.findById(ownerId)).willReturn(Optional.of(owner));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderItemsRepository.findByOrdersId(orderId)).willReturn(List.of(item));

        // when
        ChangeOrderStatusResponse response = orderService.changeOrderStatus(authUser, request, orderId);

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.COOKING, response.getOrderStatus());
    }

    @Test
    void 주문_조회_성공() {
        // given
        Long ordersId = 1L;
        Long userId = 1L;

        Users user = Users.builder().id(userId).build();
        Orders order = new Orders(user, "주소지");
        OrderItems orderItem = OrderItems.builder()
                .menus(Menus.builder().stores(Stores.builder().users(user).build()).build())
                .orderQuantity(2)
                .orders(order)
                .build();

        List<OrderItems> orderItems = List.of(orderItem);

        AuthUser authUser = new AuthUser(userId, "email@gmail.com", "테스트닉네임", UserRole.CONSUMER);

        given(orderRepository.findById(ordersId)).willReturn(Optional.of(order));
        given(orderItemsRepository.findByOrdersId(ordersId)).willReturn(orderItems);

        // when
        OrderResponse response = orderService.findOrder(authUser, ordersId);

        // then
        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
    }

    @Test
    void 주문_조회_권한_없음_실패() {
        // given
        Long ordersId = 1L;
        Long otherUserId = 2L;

        Users user = Users.builder().id(ordersId).build();
        Orders order = new Orders(user, "주소지");
        OrderItems orderItem = OrderItems.builder()
                .menus(Menus.builder().stores(Stores.builder().users(user).build()).build())
                .orders(order)
                .orderQuantity(2)
                .build();

        List<OrderItems> orderItems = List.of(orderItem);

        AuthUser authUser = new AuthUser(otherUserId, "email@gmail.com", "테스트닉네임", UserRole.CONSUMER);

        given(orderRepository.findById(ordersId)).willReturn(Optional.of(order));
        given(orderItemsRepository.findByOrdersId(ordersId)).willReturn(orderItems);

        // when & then
        assertThrows(GlobalException.class, () -> orderService.findOrder(authUser, ordersId));
    }

}