package com.delivery.igo.igo_delivery.api.cart.repository;

import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    List<CartItems> findAllByCarts(Carts carts);

    Optional<CartItems> findByCartsAndMenus(Carts carts, Menus menus);

    void deleteAllByCarts(Carts carts);

}
