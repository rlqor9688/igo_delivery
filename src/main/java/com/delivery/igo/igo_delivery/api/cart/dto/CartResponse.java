package com.delivery.igo.igo_delivery.api.cart.dto;

import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {

    private final Long userId;
    private final Long cartId;

    public static CartResponse from(Carts carts){
        return new CartResponse(carts.getUsers().getId(), carts.getId());
    }
}
