package com.delivery.igo.igo_delivery.api.user.entity;

import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("관리자"),
    CONSUMER("일반 계정"),
    OWNER("사업자 계정")
    ;

    private final String role;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.INVALID_USER_ROLE));
    }
}
