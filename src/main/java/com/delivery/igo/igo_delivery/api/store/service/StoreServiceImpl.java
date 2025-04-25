package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreListResponseDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 매장 생성
    @Transactional
    @Override
    public StoreResponseDto createStore(StoreRequestDto requestDto, Long userId) {
        Users owner = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // 사장님 권한이 아닌 경우 예외 발생
        owner.validateOwner();

        // 사장이 이미 3개의 매장을 등록한 경우 예외 발생
        long count = storeRepository.countByUsersAndStoreStatusIsNot(owner, StoreStatus.CLOSED);
        if (count >= 3) {
            throw new GlobalException(ErrorCode.MAX_STORE_LIMIT);
        }

        // DTO -> entity 변환 후 저장
        Stores store = storeRepository.save(requestDto.toEntity(owner));

        // 저장된 entity를 DTO로 변환하여 반환
        return StoreResponseDto.from(store);
    }

    // 매장 전체 조회
    @Override
    public Page<StoreListResponseDto> getStores(String storeName, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        // 잘못된 페이지 번호인 경우
        if (page < 0) {
            throw new GlobalException(ErrorCode.INVALID_PAGE_PARAMETER);
        }

        // 잘못된 페이지 크기인 경우
        if (size <= 0) {
            throw new GlobalException(ErrorCode.INVALID_SIZE_TOO_SMALL);
        }

        // 페이지 크기가 너무 큰 경우
        if (size > 100) {
            throw new GlobalException(ErrorCode.INVALID_SIZE_TOO_LARGE);
        }

        // storeName이 빈 문자열일 때 모든 매장을 조회
        // storeName이 비어있지 않으면 해당 이름을 포함하는 매장만 조회
        Page<Stores> stores = storeRepository.findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull(storeName, pageable);

        // 조회된 매장들을 StoreListResponseDto로 변환하여 반환
        return stores.map(StoreListResponseDto::from);
    }
}