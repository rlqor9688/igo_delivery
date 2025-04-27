package com.delivery.igo.igo_delivery.api.cart.entity;

import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartItemQuantityType {
    INCREASE("수량 증가") {
        @Override
        public void apply(CartItems cartItems, CartItemsRepository cartItemsRepository) {
            cartItems.increaseQuantity();
        }
    },
    DECREASE("수량 감소") {
        @Override
        public void apply(CartItems cartItems, CartItemsRepository cartItemsRepository) {
            cartItems.decreaseQuantity();
            if (cartItems.isQuantityZero()) {
                cartItemsRepository.delete(cartItems);
            }
        }
    };

    private final String description;

    public abstract void apply(CartItems cartItems, CartItemsRepository cartItemsRepository);

}