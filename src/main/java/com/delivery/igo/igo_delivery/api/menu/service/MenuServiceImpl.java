package com.delivery.igo.igo_delivery.api.menu.service;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuReadResponseDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import java.util.List;
import java.util.stream.Collectors;
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

        Users user = getUserWithAccessCheck(authUser.getId());

        Stores store = getStoreWithAccessCheck(storesId, user);

        Menus menu = Menus.of(store, requestDto.getMenuName(), requestDto.getPrice());
        Menus savedMenu = menuRepository.save(menu);

        return MenuResponseDto.from(savedMenu);
    }

    @Override
    @Transactional
    public MenuResponseDto updateMenu(AuthUser authUser, Long storesId, Long id, MenuRequestDto requestDto) {

        Users user = getUserWithAccessCheck(authUser.getId());

        Stores store = getStoreWithAccessCheck(storesId, user);

        Menus menu = getMenuWithAccessCheck(id, storesId);
        menu.updateMenu(requestDto.getMenuName(), requestDto.getPrice());

        return MenuResponseDto.from(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuReadResponseDto> findAllMenu(Long storesId) {

        Stores store = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        List<Menus> menusList = menuRepository.findMenusByStoreIdOrderByCreatedAtDesc(storesId, MenuStatus.LIVE);

        return menusList.stream().map(MenuReadResponseDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuReadResponseDto findMenuById(Long storesId, Long id) {

        Stores store = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        Menus findMenu = getMenuWithAccessCheck(id, storesId);

        return MenuReadResponseDto.from(findMenu);
    }

    @Override
    @Transactional
    public void deleteMenu(AuthUser authUser, Long storesId, Long id) {

        Users user = getUserWithAccessCheck(authUser.getId());

        Stores store = getStoreWithAccessCheck(storesId, user);

        Menus menu = getMenuWithAccessCheck(id, storesId);

        menu.delete();
    }

    private Users getUserWithAccessCheck(Long id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        user.validateDelete();
        user.validateOwner();

        return user;
    }

    private Stores getStoreWithAccessCheck(Long id, Users user) {

        Stores store = storeRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));
        store.validateOwner(user);

        return store;
    }

    private Menus getMenuWithAccessCheck(Long menuId, Long storeId) {

        Menus menu = menuRepository.findByIdAndStoresId(menuId, storeId)
                .orElseThrow(() -> new GlobalException(ErrorCode.MENU_NOT_FOUND));
        menu.validateDelete();

        return menu;
    }
}
