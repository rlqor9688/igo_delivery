package com.delivery.igo.igo_delivery.api.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindAllCartsResponseDto {

    private final Long cartsId;
    private final Long totalPrice;
    private final List<CartItemResponseDto> items;

    public static FindAllCartsResponseDto of(Long cartsId, Long totalCartPrice, List<CartItemResponseDto> cartItemsDto) {
        return new FindAllCartsResponseDto(
                cartsId,
                totalCartPrice,
                cartItemsDto
        );
    }
}
