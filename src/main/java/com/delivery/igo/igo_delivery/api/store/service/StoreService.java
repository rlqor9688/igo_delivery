package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import jakarta.transaction.Transactional;

public interface StoreService {

    // 매장 생성
    @Transactional
    StoreResponseDto createStore(StoreRequestDto requestDto, Long userId);
}
