package com.delivery.igo.igo_delivery.api.order.dto;

import com.delivery.igo.igo_delivery.api.order.entity.OrderItems;
import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import com.delivery.igo.igo_delivery.api.order.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {
    //주문 ID, 유저 닉네임, 주문한 메뉴 목록(메뉴명, 가격, 수량 포함), 주문상태, 배송지, 주문일자&수정일
    private Long id;
    private Long storeId;
    private String nickname;
    private List<OrderItemsResponse> orderItemResponse;
    private OrderStatus orderStatus;
    private String orderAddress;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static OrderResponse from(Orders orders, List<OrderItems> orderItems, Long storeId) {
        //응답할 orderItemsDto 생성 (메뉴명, 가격, 수량)
        List<OrderItemsResponse> OrderItemResponse = orderItems.stream()
                .map(item -> new OrderItemsResponse(
                        item.getMenus().getMenuName(),
                        item.getOrderItemPrice(),
                        item.getOrderQuantity().longValue()
                ))
                .toList();
        return new OrderResponse(
                orders.getId(),
                storeId,
                orders.getUsers().getNickname(),
                OrderItemResponse,
                orders.getOrderStatus(),
                orders.getOrderAddress(),
                orders.getCreatedAt(),
                orders.getModifiedAt()
        );
    }
}