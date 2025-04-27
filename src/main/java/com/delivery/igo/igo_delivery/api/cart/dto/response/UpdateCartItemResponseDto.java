package com.delivery.igo.igo_delivery.api.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartItemResponseDto {

    private final Long id;
    private final int Quantity;
}
