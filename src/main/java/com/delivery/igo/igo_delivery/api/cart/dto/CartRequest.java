package com.delivery.igo.igo_delivery.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartRequest {

    private Long menuId;
    private Integer cartQuantity;


}
