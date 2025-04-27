package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    // 매장 생성
    StoreResponseDto createStore(StoreRequestDto requestDto, Long userId);

    // 매장 전체 조회
    Page<StoreListResponseDto> getStores(String storeName, Pageable pageable);

    // 매장 단건 조회
    StoreResponseDto getStore(Long storeId);

    // 매장 수정
    StoreUpdateResponseDto updateStore(Long storeId, Long authUserId, StoreUpdateRequestDto requestDto);
}
