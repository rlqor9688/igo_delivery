package com.delivery.igo.igo_delivery.api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartRequest {

    @NotBlank(message = "{menu.id.notblank}")
    private Long menuId;

    @NotBlank(message ="{cart.quantity.notnull}")
    @Min(value = 1, message = "{cart.quantity.min}")
    private Integer cartQuantity;


}
