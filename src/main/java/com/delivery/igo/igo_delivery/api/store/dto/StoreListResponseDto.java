package com.delivery.igo.igo_delivery.api.store.dto;

import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreListResponseDto {

    private Long id;
    private String storeName;
    private String storeAddress;

    public static StoreListResponseDto from(Stores store) {
        return new StoreListResponseDto(
                store.getId(),
                store.getStoreName(),
                store.getStoreAddress()
        );
    }
}