package com.delivery.igo.igo_delivery.api.store.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {

    @Size(max = 30, message = "{store.name.size}")
    @NotBlank(message = "{store.storeName.notblank}")
    private String storeName;           // 매장명

    @Size(max = 100, message = "{store.address.size}")
    @NotBlank(message = "{store.storeAddress.notblank}")
    private String storeAddress;        // 매장 주소

    @Size(max = 20, message = "{store.phone.size}")
    @NotBlank(message = "{store.storePhoneNumber.notblank}")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "{store.phone.pattern}")
    private String storePhoneNumber;    // 매장 전화번호

    @NotNull(message = "{store.openTime.notnull}")
    private LocalTime openTime;         // 오픈 시간

    @NotNull(message = "{store.endTime.notnull}")
    private LocalTime endTime;          // 마감 시간

    @NotNull(message = "{store.minOrderPrice.notnull}")
    @Min(value = 1000, message = "{store.minOrderPrice.min}")
    private Integer minOrderPrice;      // 최소 주문 금액
}