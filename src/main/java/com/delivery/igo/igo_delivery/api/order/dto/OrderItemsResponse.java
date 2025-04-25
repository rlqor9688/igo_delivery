package com.delivery.igo.igo_delivery.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemsResponse {
    private String menuName;
    private Long orderPrice;
    private Long orderQuantity;
}

