package com.delivery.igo.igo_delivery.api.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {
    private String storeName;           // 매장명
    private String storeAddress;        // 매장 주소
    private String storePhoneNumber;    // 매장 전화번호
    private LocalTime openTime;         // 오픈 시간
    private LocalTime endTime;          // 마감 시간
    private Integer minOrderPrice;      // 최소 주문 금액
}