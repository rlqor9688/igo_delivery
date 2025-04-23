package com.delivery.igo.igo_delivery.api.auth.service;

import com.delivery.igo.igo_delivery.api.auth.dto.request.LoginRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.request.SignupRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.LoginResponseDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.SignupResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        if (userRepository.existsByEmailAndUserStatus(requestDto.getEmail(), UserStatus.LIVE)) {
            throw new AuthException(ErrorCode.USER_EXIST_EMAIL);
        }

        if (userRepository.existsByNicknameAndUserStatus(requestDto.getNickname(), UserStatus.LIVE)) {
            throw new AuthException(ErrorCode.USER_EXIST_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Users newUser = Users.of(requestDto, encodedPassword);
        Users savedUser = userRepository.save(newUser);

        return SignupResponseDto.of(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Users user = userRepository.findByEmailAndUserStatus(loginRequest.getEmail(), UserStatus.LIVE).orElseThrow(
                () -> new AuthException(ErrorCode.USER_NOT_FOUND));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.LOGIN_FAILED);
        }

        String bearerToken = jwtUtil.createToken(user);
        return new LoginResponseDto(bearerToken);
    }
}
