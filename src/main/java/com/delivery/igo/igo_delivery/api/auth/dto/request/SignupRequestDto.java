package com.delivery.igo.igo_delivery.api.auth.dto.request;

import com.delivery.igo.igo_delivery.common.annotation.EmailDuplicate;
import com.delivery.igo.igo_delivery.common.annotation.NicknameDuplicate;
import com.delivery.igo.igo_delivery.common.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "{auth.email.notblank}")
    @Email(message = "{auth.email.invalid}")
    @EmailDuplicate(message = "{auth.email.duplicate}")
    private String email;

    @NotBlank(message = "{auth.nickname.notblank}")
    @NicknameDuplicate(message = "{auth.nickname.duplicate}")
    private String nickname;

    @NotBlank(message = "{auth.password.notblank}")
    @Password(message = "{auth.password.invalid}")
    private String password;

    @NotBlank(message = "{auth.phoneNumber.notblank}")
    private String phoneNumber;

    @NotBlank(message = "{auth.address.notblank}")
    private String address;

    @NotBlank(message = "{auth.userRole.notblank}")
    private String userRole;
}