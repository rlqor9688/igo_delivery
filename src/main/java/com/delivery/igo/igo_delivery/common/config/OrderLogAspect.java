package com.delivery.igo.igo_delivery.common.config;

import com.delivery.igo.igo_delivery.api.order.dto.ChangeOrderStatusResponse;
import com.delivery.igo.igo_delivery.api.order.dto.OrderResponse;
import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderLogAspect {


    @Pointcut("execution(* com.delivery.igo.igo_delivery.api.order.service.OrderService.createOrder(..)) " +
            "|| execution(* com.delivery.igo.igo_delivery.api.order.service.OrderService.changeOrderStatus(..))")
    public void orderMethods() {}

    @AfterReturning(pointcut = "orderMethods()", returning = "result")
    public void logOrder(JoinPoint joinPoint, Object result) {
        // 요청 시각
        LocalDateTime now = LocalDateTime.now();

        Long orderId = null;
        Long storeId = null;
        OrderStatus orderStatus = null;

        if (result instanceof OrderResponse orderResponse) {
            // 주문 생성 응답 처리
            orderId = orderResponse.getId();
            storeId = orderResponse.getStoreId();
        } else if (result instanceof ChangeOrderStatusResponse changeOrderStatusResponse) {
            // 주문 상태 변경 응답 처리
            orderId = changeOrderStatusResponse.getOrderId();
            storeId = changeOrderStatusResponse.getStoreId();
            orderStatus = changeOrderStatusResponse.getOrderStatus();
        }

        if (orderStatus != null) {
            log.info("[주문 상태 변경] 시간: {}, 주문ID: {}, 가게ID: {}, 주문 상태: {}", now, orderId, storeId, orderStatus);
        } else {
            log.info("[주문 생성] 시간: {}, 주문ID: {}, 가게ID: {}", now, orderId, storeId);
        }
    }
}
