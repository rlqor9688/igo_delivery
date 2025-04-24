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

    @NotBlank(message = "매장 이름은 필수입니다.")
    private String storeName;           // 매장명

    @NotBlank(message = "매장 주소는 필수입니다.")
    private String storeAddress;        // 매장 주소

    @NotBlank(message = "매장 전화번호는 필수입니다.")
    private String storePhoneNumber;    // 매장 전화번호

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;         // 오픈 시간

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime endTime;          // 마감 시간

    @NotNull(message = "최소 주문 금액은 필수입니다.")
    @Min(value = 10000, message = "최소 주문 금액은 10000원 이상이어야 합니다.")
    private Integer minOrderPrice;      // 최소 주문 금액
}