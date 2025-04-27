package com.delivery.igo.igo_delivery.api.menu.dto.response;

import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuReadResponseDto {

    private final Long id;

    private final String menuName;

    private final Long price;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public static MenuReadResponseDto from(Menus menus) {

        return new MenuReadResponseDto(menus.getId(), menus.getMenuName(), menus.getPrice(), menus.getCreatedAt(), menus.getModifiedAt());
    }
}
