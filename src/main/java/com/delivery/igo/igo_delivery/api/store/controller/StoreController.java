package com.delivery.igo.igo_delivery.api.store.controller;

import com.delivery.igo.igo_delivery.api.store.dto.*;
import com.delivery.igo.igo_delivery.api.store.service.StoreService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.dto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    // 매장 생성
    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
            @Auth AuthUser loginUser,
            @Valid @RequestBody StoreRequestDto requestDto
    ) {
        StoreResponseDto response = storeService.createStore(requestDto, loginUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 매장 전체 조회
    @GetMapping
    public ResponseEntity<PageResponseDto<StoreListResponseDto>> getStores(
            @RequestParam(name = "storeName", defaultValue = "") String storeName,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        // 매장 목록을 페이지네이션 조회
        Page<StoreListResponseDto> storePage = storeService.getStores(storeName, pageable);

        // 조회 결과를 PageResponseDto 형태로 변환
        PageResponseDto<StoreListResponseDto> response = PageResponseDto.from(storePage);

        return ResponseEntity.ok(response);
    }

    // 매장 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDto> getStore(@PathVariable Long id) {
        StoreResponseDto response = storeService.getStore(id);
        return ResponseEntity.ok(response);
    }

    // 매장 수정
    @PutMapping("/{id}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(
            @PathVariable Long id,
            @Auth AuthUser authUser,
            @Valid @RequestBody StoreUpdateRequestDto requestDto
    ) {
        StoreUpdateResponseDto response = storeService.updateStore(id, authUser.getId(), requestDto);
        return ResponseEntity.ok(response);
    }
}