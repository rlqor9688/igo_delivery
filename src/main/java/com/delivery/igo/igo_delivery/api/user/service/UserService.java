package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.request.DeleteUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdatePasswordRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface UserService {
    UserResponseDto findUserById(Long id, AuthUser authUser);

    UserResponseDto updateUserById(Long id, AuthUser authUser, UpdateUserRequestDto requestDto);

    void updateUserPasswordById(Long id, AuthUser authUser, UpdatePasswordRequestDto requestDto);

    void deleteUser(Long id, AuthUser authUser, DeleteUserRequestDto requestDto);
}
