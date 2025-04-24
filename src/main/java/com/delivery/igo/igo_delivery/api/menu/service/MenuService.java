package com.delivery.igo.igo_delivery.api.menu.service;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface MenuService {

    MenuResponseDto createMenu(AuthUser authUser, Long storesId, MenuRequestDto requestDto);
}
