package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface UserService {
    UserResponseDto findUserById(Long id, AuthUser authUser);
}
