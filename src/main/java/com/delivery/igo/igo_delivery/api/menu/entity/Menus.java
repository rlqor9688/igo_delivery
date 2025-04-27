package com.delivery.igo.igo_delivery.api.menu.entity;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder // entity 생성 시 생성자가 아니라 Menus.builder().menus_id = "변경"
@Table(name = "menus")
public class Menus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="menus_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stores_id", nullable = false)
    private Stores stores;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private Long price;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MenuStatus menuStatus;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.menuStatus = MenuStatus.INACTIVE;
    }

    public static Menus of(Stores stores, String menuName, Long price) {

        return Menus.builder()
                .stores(stores)
                .menuName(menuName)
                .price(price)
                .menuStatus(MenuStatus.LIVE)
                .build();
    }

    public void updateMenu(String menuName, Long price) {

        this.menuName = menuName;
        this.price = price;
    }

    public void validateDelete() {
        if (menuStatus == MenuStatus.INACTIVE) {
            throw new GlobalException(ErrorCode.DELETED_MENU);
        }
    }
}
