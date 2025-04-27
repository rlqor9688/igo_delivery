package com.delivery.igo.igo_delivery.api.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequestDto {

    @NotBlank
    private String storeName;           // 매장 이름

    @NotBlank
    private String storeAddress;        // 매장 주소

    @NotBlank
    private String storePhoneNumber;    // 매장 전화번호

    @NotNull
    private LocalTime openTime;         // 영업 시작 시간

    @NotNull
    private LocalTime endTime;          // 영업 종료 시간

    @NotNull
    private Integer minOrderPrice;      // 최소 주문 금액
}