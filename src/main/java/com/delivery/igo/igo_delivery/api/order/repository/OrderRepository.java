package com.delivery.igo.igo_delivery.api.order.repository;

import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
