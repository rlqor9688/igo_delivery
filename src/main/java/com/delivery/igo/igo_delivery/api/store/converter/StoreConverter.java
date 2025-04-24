package com.delivery.igo.igo_delivery.api.store.converter;

import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;

import java.sql.Time;

public class StoreConverter {

    // 매장 생성 요청 DTO를 엔티티로 변환
    public static Stores toEntity(StoreRequestDto dto, Users owner) {
        return Stores.builder()
                .storeName(dto.getStoreName())
                .storeAddress(dto.getStoreAddress())
                .storePhoneNumber(dto.getStorePhoneNumber())
                .openTime(Time.valueOf(dto.getOpenTime()))
                .endTime(Time.valueOf(dto.getEndTime()))
                .minOrderPrice(dto.getMinOrderPrice())
                .storeStatus(StoreStatus.LIVE)
                .users(owner)
                .reviewCount(0)
                .avgRating(0.0)
                .build();
    }

    // 매장 엔티티를 응답용 DTO로 변환
    public static StoreResponseDto toDto(Stores store) {
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
