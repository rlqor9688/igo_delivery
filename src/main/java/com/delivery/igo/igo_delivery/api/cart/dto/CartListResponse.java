package com.delivery.igo.igo_delivery.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class CartListResponse {

    private final Long userId;
    private final List<CartItemResponse> cartItemResponseList;

}