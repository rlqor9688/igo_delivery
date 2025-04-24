package com.delivery.igo.igo_delivery.api.store.repository;

import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Stores, Long> {
}
