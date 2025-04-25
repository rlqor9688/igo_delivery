package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.resonse.FindUserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public FindUserResponseDto findUserById(Long id, AuthUser authUser) {
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete(); // 삭제 검증, 삭제된 상태라면 404 예외 발생

        return FindUserResponseDto.from(users);
    }
}
