package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreListResponseDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    // 매장 생성
    @Transactional
    StoreResponseDto createStore(StoreRequestDto requestDto, Long userId);

    // 매장 전체 조회
    Page<StoreListResponseDto> getStores(String name, Pageable pageable);
}
