package com.delivery.igo.igo_delivery.api.cart.dto.response;

import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCartResponseDto {

    private final Long userId;
    private final Long cartId;

    public static CreateCartResponseDto from(Carts carts){
        return new CreateCartResponseDto(carts.getUsers().getId(), carts.getId());
    }
}
