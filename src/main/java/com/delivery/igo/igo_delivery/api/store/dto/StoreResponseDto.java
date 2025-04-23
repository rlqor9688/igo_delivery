package com.delivery.igo.igo_delivery.api.store.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class StoreResponseDto {
    private Long id;
    private String storeName;
    private String storeAddress;
    private String storePhoneNumber;
    private LocalTime openTime;
    private LocalTime endTime;
    private Integer minOrderPrice;
    private String storeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
