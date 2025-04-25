package com.delivery.igo.igo_delivery.api.store.dto;

import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreListResponseDto {

    private Long id;                    // 매장 ID
    private String storeName;           // 매장 이름
    private Integer minOrderPrice;      // 최소 주문 금액
    private Integer reviewCount;        // 리뷰 수
    private Double avgRating;           // 평균 별점
    private LocalDateTime createdAt;    // 생성 일시
    private LocalDateTime modifiedAt;   // 수정 일시

    public static StoreListResponseDto from(Stores store) {
        return new StoreListResponseDto(
                store.getId(),
                store.getStoreName(),
                store.getMinOrderPrice(),
                store.getReviewCount(),
                store.getAvgRating(),
                store.getCreatedAt(),
                store.getModifiedAt()
        );
    }
}