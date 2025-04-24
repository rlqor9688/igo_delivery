package com.delivery.igo.igo_delivery.api.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    LIVE("활성 사용자"),
    INACTIVE("삭제된 사용자"),
    ;

    private final String description;

}