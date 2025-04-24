package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.converter.StoreConverter;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService{

    // 매장 저장 및 조회를 위한 Repository
    private final StoreRepository storeRepository;

    // 매장 생성
    @Override
    @Transactional
    public StoreResponseDto createStore(StoreRequestDto requestDto, Users owner) {

        // 사장님 권한이 아닌 경우 예외 발생
        if (!owner.getUserRole().equals(UserRole.OWNER)) {
            throw new GlobalException(ErrorCode.NOT_OWNER);
        }

        // 사장이 이미 3개의 매장을 등록한 경우 예외 발생
        long count = storeRepository.countByUsersAndStoreStatusIsNot(owner, com.delivery.igo.igo_delivery.api.store.entity.StoreStatus.CLOSED);
        if (count >= 3) {
            throw new IllegalStateException("매장은 최대 3개까지 운영할 수 있습니다.");
        }

        // DTO -> 엔티티 변환 후 저장
        Stores store = storeRepository.save(StoreConverter.toEntity(requestDto, owner));

        // 저장된 엔티티를 DTO로 변환하여 반환
        return StoreConverter.toDto(store);
    }
}