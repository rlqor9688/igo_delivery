package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuReadResponseDto;
import com.delivery.igo.igo_delivery.api.menu.service.MenuService;
import com.delivery.igo.igo_delivery.api.store.dto.*;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final MenuService menuService;

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    // 매장 생성
    @Override
    @Transactional
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
    @Transactional(readOnly = true)
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

    // 매장 단건 조회
    @Override
    @Transactional(readOnly = true)
    public StoreResponseDto getStore(Long storeId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        // 해당 매장의 등록된 메뉴 목록 조회
        List<MenuReadResponseDto> menus = menuService.findAllMenu(storeId);

        return StoreResponseDto.from(store, menus);
    }

    // 매장 수정
    @Override
    @Transactional
    public StoreUpdateResponseDto updateStore(Long storeId, Long authUserId, StoreUpdateRequestDto requestDto) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        // 매장의 소유자가 현재 사용자와 일치하는지 검증
        if (!store.getUsers().getId().equals(authUserId)) {
            throw new GlobalException(ErrorCode.STORE_OWNER_MISMATCH);
        }

        // 매장 정보를 요청 값으로 수정
        store.updateStoreInfo(
                requestDto.getStoreName(),
                requestDto.getStoreAddress(),
                requestDto.getStorePhoneNumber(),
                requestDto.getOpenTime(),
                requestDto.getEndTime(),
                requestDto.getMinOrderPrice()
        );

        return StoreUpdateResponseDto.builder()
                .id(store.getId())
                .build();
    }

    @Override
    @Transactional
    public void closeStore(Long storeId, Long authUserId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        // 본인 소유 매장인지 검증
        if (!store.getUsers().getId().equals(authUserId)) {
            throw new GlobalException(ErrorCode.STORE_OWNER_MISMATCH);
        }

        // 매장 폐업 처리
        store.close();
    }
}