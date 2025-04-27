package com.delivery.igo.igo_delivery.api.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequestDto {
    private String storeName;           // 매장 이름
    private String storeAddress;        // 매장 주소
    private String storePhoneNumber;    // 매장 전화번호
    private LocalTime openTime;         // 영업 시작 시간
    private LocalTime endTime;          // 영업 종료 시간
    private Integer minOrderPrice;      // 최소 주문 금액
}