package com.delivery.igo.igo_delivery.api.menu.service;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuReadResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import java.util.List;

public interface MenuService {

    MenuResponseDto createMenu(AuthUser authUser, Long storesId, MenuRequestDto requestDto);

    MenuResponseDto updateMenu(AuthUser authUser, Long storesId, Long id, MenuRequestDto requestDto);

    List<MenuReadResponseDto> findAllMenu(Long storesId);

    MenuReadResponseDto findMenuById(Long storesId, Long id);
}
