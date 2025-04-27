package com.delivery.igo.igo_delivery.api.order.controller;

import com.delivery.igo.igo_delivery.api.order.dto.*;
import com.delivery.igo.igo_delivery.api.order.service.OrderService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.dto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Auth AuthUser authUser,
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return new ResponseEntity<>(orderService.createOrder(authUser,request),HttpStatus.CREATED);
    }

    //주문 상태 변경
    @PatchMapping("/{ordersId}")
    public ResponseEntity<ChangeOrderStatusResponse> changeOrderStatus(
            @Auth AuthUser authUser,
            @Valid @RequestBody ChangeOrderStatusRequest request,
            @PathVariable Long ordersId
    ){
        return ResponseEntity.ok(orderService.changeOrderStatus(authUser,request,ordersId));
    }

    //주문 단건 조회
    @GetMapping("/{ordersId}")
    public ResponseEntity<OrderResponse> findOrder(
            @Auth AuthUser authUser,
            @PathVariable Long ordersId
    ){
        return ResponseEntity.ok(orderService.findOrder(authUser,ordersId));
    }

    //주문 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDto<OrderListResponse>> findOrderList(
            @Auth AuthUser authUser,
            //page, size 입력값 검증
            @PageableDefault(page = 0, size = 5)Pageable pageable
    ){
        Page<OrderListResponse> orderListResponse = orderService.findOrderList(authUser.getId(),pageable);
        PageResponseDto<OrderListResponse> orderPageResponse = new PageResponseDto<>(
                                    orderListResponse.getContent(),
                                    orderListResponse.getTotalElements(),
                                    orderListResponse.getNumber(),
                                    orderListResponse.getSize(),
                                    orderListResponse.getTotalPages()

        );
        return ResponseEntity.ok(orderPageResponse);
    }
}
