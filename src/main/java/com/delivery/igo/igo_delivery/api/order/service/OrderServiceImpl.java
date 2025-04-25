package com.delivery.igo.igo_delivery.api.order.service;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    //todo : 관련 로직 리팩토링, 예외 처리

    //주문 생성
    @Transactional
    @Override
    public OrderResponse createOrder(AuthUser authUser, CreateOrderRequest request) {

        Users users = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // 요청한 유저의 userRole이 고객이 아닐경우 (재확인)
        users.validateConsumer();
        // 로그인한 유저의 장바구니 호출
        Carts carts = cartRepository.findByUsersId(users.getId());
        // 장바구니 물건 목록 호출
        List<CartItems> cartItems = cartItemsRepository.findAllByCarts(carts);
        // 장바구니가 비어있을 경우 에러 출력
        if (cartItems.isEmpty()) throw new GlobalException(ErrorCode.CART_ITEM_NOT_FOUND);

        // 해당 매장 정보 호출
        Stores stores = cartItems.get(0).getMenus().getStores();

        // 영업시간 외에 주문이 들어왔을 경우 에러 출력
        Time orderTime = Time.valueOf(LocalTime.now());
        if( orderTime.before(stores.getOpenTime()) || orderTime.after(stores.getEndTime())){
            throw new GlobalException(ErrorCode.OUT_OF_OPEN_TIME);
        }

        // 주문 총 가격이 가게의 최소 주문 금액보다 작을 경우 에러 출력
        long sumPrice = cartItems.stream().mapToLong(CartItems::totalPrice).sum();
        if(sumPrice < stores.getMinOrderPrice()){
            throw new GlobalException(ErrorCode.UNDER_MIN_ORDER_PRICE);
        }
        // 새 Orders 생성 및 저장
        Orders orders = new Orders(users, request.getOrderAddress());
        orderRepository.save(orders);

        // orderItems 생성
        List<OrderItems> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItems(
                        orders,
                        cartItem
                )).toList();
        orderItemsRepository.saveAll(orderItems);

        return OrderResponse.from(orders,orderItems);
    }

    //주문상태 변경
    @Override
    @Transactional
    public ChangeOrderStatusResponse changeOrderStatus(AuthUser authUser, ChangeOrderStatusRequest request, Long ordersId) {

        // 해당 주문과 주문한 유저의 데이터 호출
        Orders orders = orderRepository.findById(ordersId)
                .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_NOT_FOUND));
        Users users = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // 요청 들어온 주문 상태
        OrderStatus requestStatus = OrderStatus.of(request.getOrderStatus());

        // 요청한 유저의 userRole이 유저 일 경우(주문 취소만 가능)
        if (authUser.getUserRole() == UserRole.CONSUMER) {
            // 요청을 날린 고객이 주문한 고객과 일치하는지 확인
            orders.validateOrderUser(users);
            // 고객은 주문 취소만 가능. 다른 요청시 에러 출력
            if (requestStatus!= OrderStatus.CANCELLED) {
                throw new GlobalException(ErrorCode.CONSUMER_CANNOT_CHANGE_STATUS);
            }
            orders.changeStatus(requestStatus);
            return ChangeOrderStatusResponse.from(orders);
        }
        // 요쳥한 유저가 매장 주인인 경우
        if (authUser.getUserRole() == UserRole.OWNER) {
            //요청을 날린 매장 주인이 해당 매장의 주인인지 확인 orderItems -> menus -> stores -> users -> id
            List<OrderItems> orderItems = orderItemsRepository.findByOrdersId(ordersId);
            Stores stores = orderItems.get(0).getMenus().getStores();
            stores.validateOwner(users);
            //매장 주인이 주문 취소를 요청했을 경우 에러 출력
            if (requestStatus == OrderStatus.CANCELLED) {
                throw new GlobalException(ErrorCode.OWNER_CANNOT_CANCEL_ORDER);
            }
            orders.changeStatus(requestStatus);
            return ChangeOrderStatusResponse.from(orders);
        }
        throw new GlobalException(ErrorCode.INVALID_USER_ROLE);
    }

    // 주문 단건 조회
    @Override
    public OrderResponse findOrder(AuthUser authUser, Long ordersId) {

        // 주문 정보, 주문 메뉴 목록, 주문 유저의 정보를 호출
        Orders orders = orderRepository.findById(ordersId)
                .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_NOT_FOUND));
        Users users = orders.getUsers();
        if (users == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }
        List<OrderItems> orderItems = orderItemsRepository.findByOrdersId(ordersId);
        if(orderItems.isEmpty()){
            throw new GlobalException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 해당 유저가 주문한 유저이거나 매장 주인인지 확인 -> 둘다 아닐시 에러
        boolean isOrderUser = authUser.getId().equals(users.getId());
        boolean isStoreOwner = orderItems.stream()
                .map(item -> item.getMenus().getStores().getUsers().getId())
                .distinct()
                .allMatch(ownerId -> ownerId.equals(authUser.getId()));

        if (isOrderUser || isStoreOwner) {
            return OrderResponse.from(orders,orderItems);
        }
        throw new GlobalException(ErrorCode.FORBIDDEN);
    }


}