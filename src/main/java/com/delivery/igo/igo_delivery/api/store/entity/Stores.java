package com.delivery.igo.igo_delivery.api.store.entity;

import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

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

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String storeAddress;

    @Column(nullable = false)
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
    private Integer reviewCount = 0; // 리뷰 수 (초기값 0)

    @Column(nullable = false)
    private Double avgRating = 0.0; // 평균 별점 (초기값 0.0)

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void validateOwner(Users user) {
        if (!Objects.equals(this.getUsers().getId(), user.getId())) {
            throw new GlobalException(ErrorCode.STORE_OWNER_MISMATCH);
        }
    }

    // 매장 정보 수정
    public void updateStoreInfo(String storeName, String storeAddress, String storePhoneNumber,
                                LocalTime openTime, LocalTime endTime, Integer minOrderPrice) {
        this.storeName = storeName;                 // 매장 이름 수정
        this.storeAddress = storeAddress;           // 매장 주소 수정
        this.storePhoneNumber = storePhoneNumber;   // 매장 전화번호 수정
        this.openTime = Time.valueOf(openTime);     // 영업 시작 시간 수정
        this.endTime = Time.valueOf(endTime);       // 영업 종료 시간 수정
        this.minOrderPrice = minOrderPrice;         // 최소 주문 금액 수정
    }
}
