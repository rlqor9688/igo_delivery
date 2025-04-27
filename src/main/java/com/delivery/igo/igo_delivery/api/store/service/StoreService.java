package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreListResponseDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    // 매장 생성
    StoreResponseDto createStore(StoreRequestDto requestDto, Long userId);

    // 매장 전체 조회
    Page<StoreListResponseDto> getStores(String storeName, Pageable pageable);

    // 매장 단건 조회
    StoreResponseDto getStore(Long storeId);
}
