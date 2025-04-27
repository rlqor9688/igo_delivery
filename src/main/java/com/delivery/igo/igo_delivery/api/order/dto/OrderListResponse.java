package com.delivery.igo.igo_delivery.api.order.dto;

import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderListResponse {

    private final String storeName;
    private final String menuName;
    private final Long orderPrice;
    private final Integer countMenuType;
    private final LocalDateTime createdAt;
    private final OrderStatus orderStatus;


    public static OrderListResponse from(String storeName,
                                         String menuName,
                                         Long orderPrice,
                                         int countMenuType,
                                         LocalDateTime createdAt,
                                         OrderStatus orderStatus
                                         ){
        return new OrderListResponse(
                storeName, menuName, orderPrice, countMenuType, createdAt, orderStatus);
    }
}
