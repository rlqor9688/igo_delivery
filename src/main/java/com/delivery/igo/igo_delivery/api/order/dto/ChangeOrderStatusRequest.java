package com.delivery.igo.igo_delivery.api.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeOrderStatusRequest {

    @NotBlank(message = "{order.status.notblank}")
    private String orderStatus;
}
