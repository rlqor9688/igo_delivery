package com.delivery.igo.igo_delivery.api.order.entity;

import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder // entity 생성 시 생성자가 아니라 Menus.builder().menus_id = "변경"
@Table(name = "orders")
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String orderAddress;

    //주문 상태를 WAITING으로 지정
    public Orders(Users users, String orderAddress){
        this.users = users;
        this.orderStatus = OrderStatus.WAITING;
        this.orderAddress = orderAddress;
    }
    // 주문 상태 변경 메서드
    public void changeStatus(OrderStatus requestOrderStatus){
        if(!orderStatus.canChangeStatus(requestOrderStatus)){
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
        this.orderStatus = requestOrderStatus;
    }

    public void validateOrderUser(Users user) {
        if (!this.getUsers().getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.ROLE_CONSUMER_FORBIDDEN);
        }
    }

    public void validateOwner(Users user) {
        if (!this.getUsers().getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.STORE_OWNER_MISMATCH);
        }
    }
}
