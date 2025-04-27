package com.delivery.igo.igo_delivery.api.order.repository;

import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findByUsersId(Long id, Pageable pageable);

    // 1. userId로 해당 유저가 소유한 store를 호출
    // 2. 해당 store의 해당 menu가 들어있는 orderItems만 호출
    // 3. 해당 orderItems에 있는 모든 orderId로 orders를 호출
    @Query("SELECT o FROM Orders o JOIN OrderItems oi ON o = oi.orders JOIN oi.menus m JOIN m.stores s WHERE s.users.id = :userId")
    Page<Orders> findByOwnerId(Long userId, Pageable pageable);


}
