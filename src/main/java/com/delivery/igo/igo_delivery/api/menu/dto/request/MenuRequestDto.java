package com.delivery.igo.igo_delivery.api.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuRequestDto {

    @NotBlank(message = "{menu.menuName.notblank}")
    private final String menuName;

    @NotNull(message = "{menu.price.notblank}")
    private final Long price;
}
