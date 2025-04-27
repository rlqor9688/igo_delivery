package com.delivery.igo.igo_delivery.api.cart.dto.response;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindAllCartsResponseDto {

    private final Long cartsId;
    private final Long totalPrice;
    private final List<CartItems> items;

    public static FindAllCartsResponseDto of(Long cartsId, Long totalCartPrice, List<CartItems> items) {
        return new FindAllCartsResponseDto(
                cartsId,
                totalCartPrice,
                items
        );
    }
}
