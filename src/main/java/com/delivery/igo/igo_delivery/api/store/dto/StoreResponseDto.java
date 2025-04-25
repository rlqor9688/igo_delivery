package com.delivery.igo.igo_delivery.api.store.dto;

import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponseDto {
    private Long id;                    // 매장 ID
    private String storeName;           // 매장 이름
    private String storeAddress;        // 매장 주소
    private String storePhoneNumber;    // 매장 전화번호
    private LocalTime openTime;         // 영업 시작 시간
    private LocalTime endTime;          // 영업 종료 시간
    private Integer minOrderPrice;      // 최소 주문 금액
    private String storeStatus;         // 매장 상태 (LIVE, CLOSED 등)
    private LocalDateTime createdAt;    // 생성 시각
    private LocalDateTime modifiedAt;   // 수정 시각

    // 매장 엔티티를 응답용 DTO로 변환
    public static StoreResponseDto from(Stores store) {
        return StoreResponseDto.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .storeAddress(store.getStoreAddress())
                .storePhoneNumber(store.getStorePhoneNumber())
                .openTime(store.getOpenTime().toLocalTime())
                .endTime(store.getEndTime().toLocalTime())
                .minOrderPrice(store.getMinOrderPrice())
                .storeStatus(store.getStoreStatus().name())
                .createdAt(store.getCreatedAt())
                .modifiedAt(store.getModifiedAt())
                .build();
    }
}