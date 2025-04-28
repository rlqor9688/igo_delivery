package com.delivery.igo.igo_delivery.api.cart.dto.request;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItemQuantityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartItemRequestDto {

    // 증가 - INCREASE
    // 감소 - DECREASE
    @NotNull
    private final CartItemQuantityType actionType;
}
