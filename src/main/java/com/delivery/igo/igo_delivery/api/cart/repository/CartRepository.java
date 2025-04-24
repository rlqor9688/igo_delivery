package com.delivery.igo.igo_delivery.api.cart.repository;

import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Carts, Long> {

    Carts findByUsersId(Long userId);

    Optional<Carts> findByUsers(Users users);
}
