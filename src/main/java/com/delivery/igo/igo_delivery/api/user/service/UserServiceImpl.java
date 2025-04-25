package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.request.DeleteUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdatePasswordRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto findUserById(Long id, AuthUser authUser) {
        Users users = getUserWithAccessCheck(id, authUser);   // 검증된 유저를 꺼내는 메서드

        return UserResponseDto.from(users);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(Long id, AuthUser authUser, UpdateUserRequestDto requestDto) {
        Users users = getUserWithAccessCheck(id, authUser);

        // 닉네임 변경 시 변경할 닉네임이 DB에 있으면 예외 발생
        if (!users.getNickname().equals(requestDto.getNickname()) &&
                userRepository.existsByNickname(requestDto.getNickname())) {
            throw new GlobalException(ErrorCode.USER_EXIST_NICKNAME);
        }

        users.updateBy(requestDto);

        return UserResponseDto.from(users);
    }

    @Override
    @Transactional
    public void updateUserPasswordById(Long id, AuthUser authUser, UpdatePasswordRequestDto requestDto) {
        // 기존 비밀번호와 같은 비밀번호로 요청이 들어올 경우 예외 발생
        if (Objects.equals(requestDto.getOldPassword(), requestDto.getNewPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_DUPLICATED);
        }

        Users users = getUserWithAccessCheck(id, authUser);

        if (!passwordEncoder.matches(requestDto.getOldPassword(), users.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        users.updatePassword(encodedPassword);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, AuthUser authUser, DeleteUserRequestDto requestDto) {
        Users users = getUserWithAccessCheck(id, authUser);   // 검증된 유저를 꺼내는 메서드

        if (!passwordEncoder.matches(requestDto.getPassword(), users.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        users.delete();
    }

    // UserServiceImpl 모든 메서드에서 공통적으로 사용되는 User 검증을 private 메서드화 하여 재사용하도록 변경
    private Users getUserWithAccessCheck(Long id, AuthUser authUser) {
        // 유저가 없으면 예외
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete();         // 삭제 되었는지 검증
        return users;
    }
}
