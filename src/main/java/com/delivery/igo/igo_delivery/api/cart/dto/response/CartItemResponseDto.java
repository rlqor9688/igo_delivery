package com.delivery.igo.igo_delivery.api.cart.dto.response;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDto {
    private final Long cartItemId;
    private final Long menuId;
    private final String menuName;
    private final Long price;
    private final int cartQuantity;

    public static CartItemResponseDto from(CartItems cartItems) {
        return new CartItemResponseDto(
                cartItems.getId(),
                cartItems.getMenus().getId(),
                cartItems.getMenus().getMenuName(),
                cartItems.getCartPrice(),
                cartItems.getCartQuantity()
        );
    }
}
