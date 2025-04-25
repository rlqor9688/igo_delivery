package com.delivery.igo.igo_delivery.api.order.repository;

import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findByOrdersId(Long ordersId);
}
