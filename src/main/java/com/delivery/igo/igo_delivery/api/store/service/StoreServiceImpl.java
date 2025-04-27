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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 500;

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

        // 페이지가 기본값 미만인 경우 기본값으로 조정
        if (page < DEFAULT_PAGE_NUMBER) {
            page = DEFAULT_PAGE_NUMBER;
        }

        // 사이즈가 최소 허용값 미만인 경우 기본값으로 조정
        if (size < MIN_PAGE_SIZE) {
            size = DEFAULT_PAGE_SIZE;
        }

        // 사이즈가 최대 허용값을 초과하면 최대값으로 조정
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        // 보정된 page, size 값으로 Pageable 객체 생성
        Pageable correctedPageable = PageRequest.of(page, size);

        // 삭제되지 않은 매장 중 이름으로 조회
        Page<Stores> stores = storeRepository.findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull(storeName, correctedPageable);

        // 조회된 매장들을 StoreListResponseDto로 변환하여 반환
        return stores.map(StoreListResponseDto::from);
    }
}