package com.delivery.igo.igo_delivery.api.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {

    @NotBlank(message = "{store.storeName.notblank}")
    private String storeName;           // 매장명

    @NotBlank(message = "{store.storeAddress.notblank}")
    private String storeAddress;        // 매장 주소

    @NotBlank(message = "{store.storePhoneNumber.notblank}")
    private String storePhoneNumber;    // 매장 전화번호

    @NotNull(message = "{store.openTime.notnull}")
    private LocalTime openTime;         // 오픈 시간

    @NotNull(message = "{store.endTime.notnull}")
    private LocalTime endTime;          // 마감 시간

    @NotNull(message = "{store.minOrderPrice.notnull}")
    @Min(value = 1000, message = "{store.minOrderPrice.min}")
    private Integer minOrderPrice;      // 최소 주문 금액
}