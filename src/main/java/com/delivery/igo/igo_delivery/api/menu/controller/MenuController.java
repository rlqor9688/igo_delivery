package com.delivery.igo.igo_delivery.api.menu.controller;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuReadResponseDto;
import com.delivery.igo.igo_delivery.api.menu.service.MenuService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

        MenuResponseDto responseDto = menuService.createMenu(authUser, storesId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponseDto> updateMenu(@Auth AuthUser authUser, @PathVariable Long storesId,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody MenuRequestDto requestDto) {

        MenuResponseDto responseDto = menuService.updateMenu(authUser, storesId, id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<MenuReadResponseDto>> findAllMenu(@PathVariable Long storesId) {

        List<MenuReadResponseDto> allMenu = menuService.findAllMenu(storesId);

        return ResponseEntity.ok(allMenu);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuReadResponseDto> findMenuById(@PathVariable Long storesId, @PathVariable Long id) {

        MenuReadResponseDto responseDto = menuService.findMenuById(storesId, id);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@Auth AuthUser authUser, @PathVariable Long storesId, @PathVariable Long id) {

        menuService.deleteMenu(authUser, storesId, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
