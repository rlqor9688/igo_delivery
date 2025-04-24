package com.delivery.igo.igo_delivery.api.auth.service;

import com.delivery.igo.igo_delivery.api.auth.dto.request.LoginRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.request.SignupRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.LoginResponseDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.SignupResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface AuthService {
    SignupResponseDto signup(SignupRequestDto signupRequest);

    LoginResponseDto login(LoginRequestDto loginRequest);

    void logout(AuthUser authUser);
}
