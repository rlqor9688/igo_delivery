package com.delivery.igo.igo_delivery.api.store.repository;

import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Stores, Long> {
}

public interface StoreRepository extends JpaRepository<Stores, Long> {
    long countByUsersAndStoreStatusIsNot(Users user, StoreStatus status);
}