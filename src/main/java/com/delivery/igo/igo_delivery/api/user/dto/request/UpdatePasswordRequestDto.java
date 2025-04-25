package com.delivery.igo.igo_delivery.api.user.dto.request;

import com.delivery.igo.igo_delivery.common.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = "{auth.password.notblank}")
    @Password(message = "{auth.password.invalid}")
    private final String oldPassword;

    @NotBlank(message = "{auth.password.notblank}")
    @Password(message = "{auth.password.invalid}")
    private final String newPassword;
}
