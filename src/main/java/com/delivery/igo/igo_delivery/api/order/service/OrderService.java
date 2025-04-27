package com.delivery.igo.igo_delivery.api.order.service;

import com.delivery.igo.igo_delivery.api.order.dto.*;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderService {

    OrderResponse createOrder(AuthUser authUser, CreateOrderRequest request);

    ChangeOrderStatusResponse changeOrderStatus(AuthUser authUser, ChangeOrderStatusRequest request, Long ordersId);

    OrderResponse findOrder(AuthUser authUser, Long ordersId);

    Page<OrderListResponse> findOrderList(Long authId, Pageable pageable);
}
