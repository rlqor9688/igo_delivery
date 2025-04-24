package com.delivery.igo.igo_delivery.api.user.dto.resonse;

import com.delivery.igo.igo_delivery.api.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String phoneNumber;
    private final String address;
    private final String role;
    private final LocalDateTime createAt;
    private final LocalDateTime modifiedAt;

    public static UserResponseDto from(Users findUser) {
        return new UserResponseDto(
                findUser.getId(),
                findUser.getEmail(),
                findUser.getNickname(),
                findUser.getPhoneNumber(),
                findUser.getAddress(),
                findUser.getUserRole().toString(),
                findUser.getCreatedAt(),
                findUser.getModifiedAt()
        );
    }


}
