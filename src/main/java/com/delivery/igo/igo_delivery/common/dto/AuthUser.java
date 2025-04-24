package com.delivery.igo.igo_delivery.common.dto;

import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.common.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole userRole;

    public static AuthUser of(String token, JwtUtil jwtUtil) {
        return new AuthUser(jwtUtil.getUserId(token),
                jwtUtil.getUserEmail(token),
                jwtUtil.getUserNickname(token),
                jwtUtil.getUserRole(token));
    }
}

