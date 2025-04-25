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
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete(); // 삭제 검증, 삭제된 상태라면 404 예외 발생

        return UserResponseDto.from(users);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(Long id, AuthUser authUser, UpdateUserRequestDto requestDto) {
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete();         // 삭제 검증

        // 닉네임 변경 시 변경할 닉네임이 DB에 있으면 예외 발생
        if (!users.getNickname().equals(requestDto.getNickname()) &&
                userRepository.existsByNickname(requestDto.getNickname())) {
            throw new GlobalException(ErrorCode.USER_EXIST_NICKNAME);
        }

        users.updateBy(requestDto);     // 업데이트

        return UserResponseDto.from(users);
    }

    @Override
    @Transactional
    public void updateUserPasswordById(Long id, AuthUser authUser, UpdatePasswordRequestDto requestDto) {
        // 기존 비밀번호와 같은 비밀번호로 요청이 들어올 경우 예외 발생
        if (Objects.equals(requestDto.getOldPassword(), requestDto.getNewPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_DUPLICATED);
        }

        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete();         // 삭제 검증

        if (!passwordEncoder.matches(requestDto.getOldPassword(), users.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        users.updatePassword(encodedPassword);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, AuthUser authUser, DeleteUserRequestDto requestDto) {
        Users users = userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        users.validateAccess(authUser); // 로그인한 본인인지 검증
        users.validateDelete();

        if (!passwordEncoder.matches(requestDto.getPassword(), users.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        users.delete();
    }
}
