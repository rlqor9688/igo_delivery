package com.delivery.igo.igo_delivery.api.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private final String bearerToken;

}
