package com.delivery.igo.igo_delivery.api.store.entity;

import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder // entity 생성 시 생성자가 아니라 Menus.builder().menus_id = "변경"
@Table(name = "stores")
public class Stores extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stores_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @Column(nullable = false, length = 30)
    private String storeName;

    @Column(nullable = false, length = 100)
    private String storeAddress;

    @Column(nullable = false, length = 20)
    private String storePhoneNumber;

    @Column(nullable = false)
    private Time openTime;

    @Column(nullable = false)
    private Time endTime;

    @Column(nullable = false)
    private Integer minOrderPrice;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Double avgRating = 0.0;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
