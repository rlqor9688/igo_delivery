package com.delivery.igo.igo_delivery.api.menu.repository;

import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menus, Long> {

    Optional<Menus> findByIdAndStoresId(Long id, Long storesId);
}
