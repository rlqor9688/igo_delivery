package com.delivery.igo.igo_delivery.api.user.dto.request;

import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 수정 요청 값이기 때문에 클라이언트에서 null이 오지못하도록 기본 메시지를 출력
 */
@Getter
@AllArgsConstructor
public class UpdateUserRequestDto {

    @NotBlank
    private final String nickname;

    @NotBlank
    private final String phoneNumber;

    @NotBlank
    private final String address;

    @NotNull
    private final UserRole role;

}
