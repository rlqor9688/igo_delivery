package com.delivery.igo.igo_delivery.api.menu.dto.response;

import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuResponseDto {

    private final Long id;

    public static MenuResponseDto from(Menus menus) {

        return new MenuResponseDto(menus.getId());
    }
}
