package com.delivery.igo.igo_delivery.api.menu.controller;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.service.MenuService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores/{storesId}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponseDto> createMenu(@Auth AuthUser authUser, @PathVariable Long storesId,
                                                      @Valid @RequestBody MenuRequestDto requestDto) {

        MenuResponseDto menu = menuService.createMenu(authUser, storesId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(menu);
    }
}
