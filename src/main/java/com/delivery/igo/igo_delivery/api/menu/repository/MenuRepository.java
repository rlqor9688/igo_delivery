package com.delivery.igo.igo_delivery.api.menu.repository;

import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menus, Long> {
}
