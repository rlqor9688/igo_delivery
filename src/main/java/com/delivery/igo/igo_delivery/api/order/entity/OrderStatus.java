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

    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(orderStatus))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.INVALID_ORDER_STATUS));
    }

    // 주문 상태 변경 관련 로직 -> true 리턴시 orders의 주문 상태 변경
    public boolean canChangeStatus(OrderStatus orderStatus){
        return switch (this) {
            case WAITING -> orderStatus == COOKING || orderStatus == CANCELLED || orderStatus == REFUSED;
            case COOKING -> orderStatus == IN_DELIVERY || orderStatus == CANCELLED || orderStatus == REFUSED;
            case IN_DELIVERY -> orderStatus == COMPLETE || orderStatus == CANCELLED || orderStatus == REFUSED;
            case COMPLETE -> throw new GlobalException(ErrorCode.COMPLETE_ORDER);
            case CANCELLED -> throw new GlobalException(ErrorCode.CANCELLED_ORDER);
            default -> throw new GlobalException(ErrorCode.REFUSED_ORDER);
        };
    }
}
