package com.delivery.igo.igo_delivery.api.order.entity;

import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    WAITING("대기중"),
    COOKING("조리중"),
    IN_DELIVERY("배달중"),
    COMPLETE("배달 완료"),
    CANCELLED("주문 취소"),
    REFUSED("주문 거부");

    private final String orderStatus;

    //todo : 관련 에러 코드 추가
    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(orderStatus))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.FORBIDDEN));
    }

}
