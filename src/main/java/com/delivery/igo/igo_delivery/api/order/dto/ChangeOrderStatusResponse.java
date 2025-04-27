package com.delivery.igo.igo_delivery.api.order.dto;

import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class ChangeOrderStatusResponse {

    private Long orderId;
    private Long storeId;
    private String nickname;
    private OrderStatus orderStatus;
    private String orderAddress;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static ChangeOrderStatusResponse from(Orders orders, Long storeId) {
        return new ChangeOrderStatusResponse(
                orders.getId(),
                storeId,
                orders.getUsers().getNickname(),
                orders.getOrderStatus(),
                orders.getOrderAddress(),
                orders.getCreatedAt(),
                orders.getModifiedAt()
        );
    }
}