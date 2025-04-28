package com.delivery.igo.igo_delivery.api.order.service;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.order.dto.*;
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
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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

    @InjectMocks
    private OrderServiceImpl orderService;
    private AuthUser authConsumer;
    private AuthUser authOwner;
    private AuthUser otherConsumer;
    private Users users;
    private Users owners;
    private Users otherUser;
    private Carts carts;
    private Menus menus;
    private CartItems cartItems;
    private Orders orders;
    private OrderItems orderItems;
    private Stores stores;
    private List<OrderItems> orderItemsList;
    private Pageable pageable;

    @BeforeEach
    public void setUp(){
        authConsumer = new AuthUser(1L,"test1@email.com", "테스트유저", UserRole.CONSUMER);
        authOwner = new AuthUser(2L,"test2@email.com", "테스트매장주", UserRole.OWNER);
        otherConsumer = new AuthUser(3L, "test3@email.com", "테스트유저2", UserRole.CONSUMER);
        users = Users.builder()
                .id(1L)
                .email("test1@email.com")
                .userRole(UserRole.CONSUMER)
                .nickname("테스트유저")
                .build();

        owners = Users.builder()
                .id(2L)
                .email("test2@email.com")
                .userRole(UserRole.OWNER)
                .nickname("테스트매장주")
                .build();

        otherUser = Users.builder()
                .id(3L)
                .email("test3@email.com")
                .userRole(UserRole.CONSUMER)
                .nickname("테스트유저2")
                .build();

        LocalTime localOpenTime = LocalTime.of(0, 1);
        LocalTime localEndTime = LocalTime.of(23, 59);

        // LocalTime을 Time으로 변환
        Time openTime = Time.valueOf(localOpenTime);
        Time endTime = Time.valueOf(localEndTime);

        stores = Stores.builder()
                .id(1L)
                .users(owners)
                .openTime(openTime)
                .endTime(endTime)
                .minOrderPrice(15000).build();

        menus = Menus.builder()
                .id(1L)
                .menuName("치킨")
                .price(15000L)
                .stores(stores)
                .build();

        carts = Carts.builder()
                .users(users)
                .build();

        cartItems = CartItems.builder()
                .menus(menus)
                .carts(carts)
                .cartPrice(menus.getPrice())
                .cartQuantity(2)
                .build();

        orders = Orders.builder()
                .id(1L)
                .users(users)
                .orderStatus(OrderStatus.WAITING)
                .orderAddress("주소지").build();

        orderItems = OrderItems.builder()
                .menus(menus)
                .orderQuantity(2)
                .orderItemPrice(15000L)
                .orders(orders)
                .build();
        orderItemsList = List.of(orderItems);
        pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdAt")));
    }

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest("서울시 마포구");

        given(userRepository.findById(userId)).willReturn(Optional.of(users));
        given(cartRepository.findByUsersId(userId)).willReturn(carts);
        given(cartItemsRepository.findAllByCarts(carts)).willReturn(List.of(cartItems));
        given(orderRepository.save(any(Orders.class))).willReturn(orders);
        given(orderItemsRepository.saveAll(anyList())).willReturn(null); // void 리턴

        // when

        OrderResponse response = orderService.createOrder(authConsumer, request);

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

        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest("CANCELLED");

        given(userRepository.findById(userId)).willReturn(Optional.of(users));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(orders));
        given(orderItemsRepository.findByOrdersId(orderId)).willReturn(List.of(orderItems));

        // when
        ChangeOrderStatusResponse response = orderService.changeOrderStatus(authConsumer, request, orderId);

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
    }


    @Test
    void 매장주인_주문_상태_변경_성공() {
        // given
        Long ownerId = 2L;
        Long orderId = 1L;
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest("COOKING");

        given(userRepository.findById(ownerId)).willReturn(Optional.of(owners));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(orders));
        given(orderItemsRepository.findByOrdersId(orderId)).willReturn(List.of(orderItems));

        // when
        ChangeOrderStatusResponse response = orderService.changeOrderStatus(authOwner, request, orders.getId());

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.COOKING, response.getOrderStatus());
    }

    @Test
    void 주문_상태_변경_권한없음() {
        Long userId = 1L;
        Long orderId = 1L;
        // given
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest("COOKING");

        given(userRepository.findById(userId)).willReturn(Optional.of(users));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(orders));

        GlobalException exception = assertThrows(GlobalException.class, () -> orderService.changeOrderStatus(authConsumer, request,orders.getId()));
        assertEquals(ErrorCode.CONSUMER_CANNOT_CHANGE_STATUS,exception.getErrorCode());

    }

    @Test
    void 주문_조회_성공() {
        // given
        Long ordersId = 1L;

        given(orderRepository.findById(ordersId)).willReturn(Optional.of(orders));
        given(orderItemsRepository.findByOrdersId(ordersId)).willReturn(orderItemsList);

        // when
        OrderResponse response = orderService.findOrder(authConsumer, ordersId);

        // then
        assertNotNull(response);
        assertEquals(orders.getId(), response.getId());
    }

    @Test
    void 주문_조회_권한_없음_실패() {
        // given
        Long ordersId = 1L;

        given(orderRepository.findById(ordersId)).willReturn(Optional.of(orders));
        given(orderItemsRepository.findByOrdersId(ordersId)).willReturn(orderItemsList);

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> orderService.findOrder(otherConsumer, ordersId));
        assertEquals(ErrorCode.FORBIDDEN,exception.getErrorCode());
    }

    @Test
    void 주문_목록_조회_고객_성공() {
        Long userId = 1L;
        Long orderId = 1L;
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(users));
        given(orderRepository.findByUsersId(userId, pageable)).willReturn(new PageImpl<>(List.of(orders)));
        given(orderItemsRepository.findByOrdersId(orderId)).willReturn(List.of(orderItems));

        // when
        Page<OrderListResponse> result = orderService.findOrderList(1L, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
}