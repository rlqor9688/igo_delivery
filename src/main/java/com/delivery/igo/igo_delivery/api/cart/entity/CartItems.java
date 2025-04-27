package com.delivery.igo.igo_delivery.api.cart.entity;

import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder // entity 생성 시 생성자가 아니라 Menus.builder().menus_id = "변경"
@Table(name = "cart_items")
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cart_items_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menus_id", nullable = false)
    private Menus menus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carts_id", nullable = false)
    private Carts carts;

    @Column(nullable = false)
    private Long cartPrice;

    @Column(nullable = false)
    private Integer cartQuantity;

    public CartItems(Menus menus, Carts carts, Long cartPrice, Integer cartQuantity){
        this.menus = menus;
        this.carts = carts;
        this.cartPrice = cartPrice;
        this.cartQuantity = cartQuantity;
    }

    public void addQuantity(Integer cartQuantity){
        this.cartQuantity += cartQuantity;
    }

    public long totalPrice() {
        return cartPrice * cartQuantity.longValue();
    }

    public void increaseQuantity() {
        cartQuantity++;
    }

    public void decreaseQuantity() {
        cartQuantity--;
    }

    public boolean isQuantityZero() {
        return cartQuantity <= 0;
    }
}
