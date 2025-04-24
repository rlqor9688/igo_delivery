package com.delivery.igo.igo_delivery.api.menu.service;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import com.delivery.igo.igo_delivery.common.validation.StoreValidator;
import com.delivery.igo.igo_delivery.common.validation.UserValidator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    private final UserValidator userValidator;

    private final StoreValidator storeValidator;

    @Override
    @Transactional
    public MenuResponseDto createMenu(AuthUser authUser, Long storesId, MenuRequestDto requestDto) {

        Users user = userValidator.validateOwner(authUser.getId());

        Stores store = storeValidator.validateStoreOwner(storesId, user);

        Menus menu = Menus.of(store, requestDto);
        Menus savedMenu = menuRepository.save(menu);

        return MenuResponseDto.of(savedMenu);
    }

    @Override
    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long storesId, Long id, MenuRequestDto requestDto) {

        Users user = userValidator.validateOwner(authUser.getId());

        Stores store = storeValidator.validateStoreOwner(storesId, user);

        Menus menu = menuRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.MENU_NOT_FOUND));
        menu.updateMenu(requestDto);

        return MenuResponseDto.of(menu);
    }
}
