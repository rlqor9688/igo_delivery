package com.delivery.igo.igo_delivery.api.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreUpdateResponseDto {
    private Long id;    // 매장 ID
}