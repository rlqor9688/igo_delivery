package com.delivery.igo.igo_delivery.api.store.repository;

import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Stores, Long> {
    long countByUsersAndStoreStatusIsNot(Users user, StoreStatus status);

    Page<Stores> findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);
}