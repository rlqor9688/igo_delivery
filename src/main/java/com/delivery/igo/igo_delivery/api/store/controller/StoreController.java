package com.delivery.igo.igo_delivery.api.store.controller;

import com.delivery.igo.igo_delivery.api.store.dto.StoreListResponseDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.service.StoreService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> getStores(
            @RequestParam(name = "storeName", defaultValue = "") String storeName,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<StoreListResponseDto> storePage = storeService.getStores(storeName, pageable);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stores", storePage.getContent());
        result.put("totalElements", storePage.getTotalElements());
        result.put("page", storePage.getNumber());
        result.put("size", storePage.getSize());
        result.put("totalPages", storePage.getTotalPages());

        return ResponseEntity.ok(result);
    }
}