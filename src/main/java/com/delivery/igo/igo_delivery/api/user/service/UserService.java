package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.resonse.FindUserResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface UserService {
    FindUserResponseDto findUserById(Long id, AuthUser authUser);
}
