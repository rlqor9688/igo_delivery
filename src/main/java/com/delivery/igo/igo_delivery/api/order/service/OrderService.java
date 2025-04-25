package com.delivery.igo.igo_delivery.api.order.service;

import com.delivery.igo.igo_delivery.api.order.dto.ChangeOrderStatusRequest;
import com.delivery.igo.igo_delivery.api.order.dto.ChangeOrderStatusResponse;
import com.delivery.igo.igo_delivery.api.order.dto.CreateOrderRequest;
import com.delivery.igo.igo_delivery.api.order.dto.OrderResponse;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;


public interface OrderService {

    OrderResponse createOrder(AuthUser authUser, CreateOrderRequest request);

    ChangeOrderStatusResponse changeOrderStatus(AuthUser authUser, ChangeOrderStatusRequest request, Long ordersId);

    OrderResponse findOrder(AuthUser authUser, Long ordersId);
}
