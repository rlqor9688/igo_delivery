package com.delivery.igo.igo_delivery.api.menu.service;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    private final UserRepository userRepository;

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public MenuResponseDto createMenu(AuthUser authUser, Long storesId, MenuRequestDto requestDto) {

        Users user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        user.validateDelete();
        user.validateOwner();

        Stores store = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));
        store.validateOwner(user);

        Menus menu = Menus.of(store, requestDto);
        Menus savedMenu = menuRepository.save(menu);

        return MenuResponseDto.of(savedMenu);
    }

    @Override
    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long storesId, Long id, MenuRequestDto requestDto) {

        Users user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        user.validateDelete();
        user.validateOwner();

        Stores store = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));
        store.validateOwner(user);

        Menus menu = menuRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.MENU_NOT_FOUND));
        menu.updateMenu(requestDto);

        return MenuResponseDto.of(menu);
    }
}
