package com.delivery.igo.igo_delivery.api.cart.entity;

import com.delivery.igo.igo_delivery.api.order.entity.OrderStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
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
@Table(name = "carts")
public class Carts extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="carts_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    public Carts(Users users){
        this.users = users;
    }
}
