package com.delivery.igo.igo_delivery.api.menu.repository;

import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menus, Long> {

    Optional<Menus> findByIdAndStoresId(Long id, Long storesId);

    @Query("SELECT m FROM Menus m WHERE m.stores.id = :storesId AND m.menuStatus = :menuStatus ORDER BY m.createdAt DESC")
    List<Menus> findMenusByStoreIdOrderByCreatedAtDesc(@Param("storesId") Long storesId,
                                                       @Param("menuStatus") MenuStatus menuStatus);
}
