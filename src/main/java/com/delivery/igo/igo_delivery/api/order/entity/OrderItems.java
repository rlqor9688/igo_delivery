package com.delivery.igo.igo_delivery.api.order.entity;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
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
@Table(name = "order_items")
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_items_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menus_id", nullable = false)
    private Menus menus;

    @Column(nullable = false)
    private Long orderItemPrice;

    @Column(nullable = false)
    private Integer orderQuantity;

    public OrderItems(Orders orders, CartItems cartItems){
        this.orders= orders;
        this.menus = cartItems.getMenus();
        this.orderQuantity = cartItems.getCartQuantity();
    }
}
