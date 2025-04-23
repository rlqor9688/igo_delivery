package com.delivery.igo.igo_delivery.api.auth.dto.response;

import com.delivery.igo.igo_delivery.api.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponseDto {

    private final Long id;
    private final String email;
    private final String nickName;

    public static SignupResponseDto of (Users savedUser) {
        return new SignupResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname()
        );
    }
}
