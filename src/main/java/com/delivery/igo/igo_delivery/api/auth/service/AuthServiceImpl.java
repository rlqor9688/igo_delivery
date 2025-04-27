package com.delivery.igo.igo_delivery.api.auth.service;

import com.delivery.igo.igo_delivery.api.auth.dto.request.LoginRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.request.SignupRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.LoginResponseDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.SignupResponseDto;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
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
    private final CartRepository cartRepository;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new AuthException(ErrorCode.USER_EXIST_EMAIL);
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new AuthException(ErrorCode.USER_EXIST_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        UserRole userRole = UserRole.of(requestDto.getUserRole());
        Users newUser = buildUser(requestDto, encodedPassword, userRole);

        Users savedUser = userRepository.save(newUser);
        cartRepository.save(new Carts(savedUser));

        return SignupResponseDto.of(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Users user = userRepository.findByEmailAndUserStatus(loginRequest.getEmail(), UserStatus.LIVE).orElseThrow(
                () -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.LOGIN_FAILED);
        }

        String bearerToken = jwtUtil.createToken(user);
        return new LoginResponseDto(bearerToken);
    }

    @Override
    @Transactional
    public void logout(AuthUser authUser) {
        Users user = userRepository.findByEmail(authUser.getEmail())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (!user.getId().equals(authUser.getId())) {
            throw new AuthException(ErrorCode.FORBIDDEN);
        }
    }

    private Users buildUser(SignupRequestDto requestDto, String encodedPassword, UserRole userRole) {
        return Users.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .phoneNumber(requestDto.getPhoneNumber())
                .password(encodedPassword)
                .address(requestDto.getAddress())
                .userRole(userRole)
                .userStatus(UserStatus.LIVE)
                .build();
    }
}
