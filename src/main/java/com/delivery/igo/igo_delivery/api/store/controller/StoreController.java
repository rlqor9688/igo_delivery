package com.delivery.igo.igo_delivery.api.store.controller;

import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.service.StoreService;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final UserRepository userRepository;

    // 매장 생성
    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
            @Auth AuthUser loginUser,
            @Valid @RequestBody StoreRequestDto requestDto
    ) {
        StoreResponseDto response = storeService.createStore(requestDto, loginUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}