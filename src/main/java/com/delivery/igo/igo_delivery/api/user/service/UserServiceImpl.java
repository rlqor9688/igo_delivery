package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto findUserById(Long id, AuthUser authUser) {
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete(); // 삭제 검증, 삭제된 상태라면 404 예외 발생

        return UserResponseDto.from(users);
    }

    @Override
    public UserResponseDto updateUserById(Long id, AuthUser authUser, UpdateUserRequestDto requestDto) {
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 변경 시 변경할 닉네임이 DB에 있으면 예외 발생
        if (!users.getNickname().equals(requestDto.getNickname()) &&
                userRepository.existsByNickname(requestDto.getNickname())) {
            throw new GlobalException(ErrorCode.USER_EXIST_NICKNAME);
        }

        users.validateAccess(authUser); // 로그인한 보인인지 검증
        users.validateDelete();         // 삭제 검증
        users.updateBy(requestDto);     // 업데이트

        return UserResponseDto.from(users);
    }
}
