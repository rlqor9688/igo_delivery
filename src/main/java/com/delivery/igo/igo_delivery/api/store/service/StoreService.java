package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.Users;

public interface StoreService {
    StoreResponseDto createStore(StoreRequestDto requestDto, Users owner);
}
