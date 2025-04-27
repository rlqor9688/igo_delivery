package com.delivery.igo.igo_delivery.api.auth.service;

import com.delivery.igo.igo_delivery.api.auth.dto.request.LoginRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.request.SignupRequestDto;
import com.delivery.igo.igo_delivery.api.auth.dto.response.LoginResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplUnitTest {

    public static final SignupRequestDto SIGNUP_REQUEST_DTO = new SignupRequestDto(
            "email@naver.com",
            "닉네임",
            "qwer1234!@#$",
            "010-1111-2222",
            "주소",
            "consumer");

    public static final LoginRequestDto LOGIN_REQUEST_DTO = new LoginRequestDto(
            "email@naver.com",
            "qwer1234!@#$");

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void 회원가입시_이메일_중복_발생시_예외발생_에러코드_USER_EXIST_EMAIL() {
        // given
        given(userRepository.existsByEmail(SIGNUP_REQUEST_DTO.getEmail())).willReturn(true);

        // when & then
        AuthException authException = assertThrows(AuthException.class, () -> authService.signup(SIGNUP_REQUEST_DTO));
        assertEquals(ErrorCode.USER_EXIST_EMAIL, authException.getErrorCode());
        verify(userRepository).existsByEmail(SIGNUP_REQUEST_DTO.getEmail());
    }

    @Test
    void 회원가입시_닉네임_중복_발생시_예외발생_에러코드_USER_EXIST_NICKNAME() {
        // given
        given(userRepository.existsByNickname(SIGNUP_REQUEST_DTO.getNickname())).willReturn(true);

        // when & then
        AuthException authException = assertThrows(AuthException.class, () -> authService.signup(SIGNUP_REQUEST_DTO));
        assertEquals(ErrorCode.USER_EXIST_NICKNAME, authException.getErrorCode());
        verify(userRepository).existsByNickname(SIGNUP_REQUEST_DTO.getNickname());
    }

    @Test
    void 로그인이_성공하면_토큰이_발급됨() {
        // given
        Users mockUser = Users.builder()
                .email(SIGNUP_REQUEST_DTO.getEmail())
                .nickname(SIGNUP_REQUEST_DTO.getNickname())
                .phoneNumber(SIGNUP_REQUEST_DTO.getPhoneNumber())
                .password("encodedPassword")
                .address(SIGNUP_REQUEST_DTO.getAddress())
                .userRole(UserRole.of(SIGNUP_REQUEST_DTO.getUserRole()))
                .userStatus(UserStatus.LIVE)
                .build();

        given(userRepository.findByEmailAndUserStatus(SIGNUP_REQUEST_DTO.getEmail(), UserStatus.LIVE)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword())).willReturn(true);
        given(jwtUtil.createToken(mockUser)).willReturn("mockToken");

        // when
        LoginResponseDto loginResponseDto = authService.login(LOGIN_REQUEST_DTO);

        // then
        assertEquals("mockToken", loginResponseDto.getBearerToken());
        verify(userRepository).findByEmailAndUserStatus(SIGNUP_REQUEST_DTO.getEmail(), UserStatus.LIVE);
        verify(passwordEncoder).matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword());
        verify(jwtUtil).createToken(mockUser);
    }

    @Test
    void 로그인시_유저가_존재하지않으면_예외발생_예러코드_USER_NOT_FOUND() {  // 비활성화된 유저도 조회가 안되기 때문에 함께 검증됨
        // given
        given(userRepository.findByEmailAndUserStatus(LOGIN_REQUEST_DTO.getEmail(), UserStatus.LIVE)).willReturn(Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(LOGIN_REQUEST_DTO));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findByEmailAndUserStatus(LOGIN_REQUEST_DTO.getEmail(), UserStatus.LIVE);
    }

    @Test
    void 로그인시_비밀번호가_일지하지않으면_예외발생_에러코드_LOGIN_FALIED() {
        // given
        Users mockUser = Users.builder()
                .email(SIGNUP_REQUEST_DTO.getEmail())
                .nickname(SIGNUP_REQUEST_DTO.getNickname())
                .phoneNumber(SIGNUP_REQUEST_DTO.getPhoneNumber())
                .password("encodedPassword")
                .address(SIGNUP_REQUEST_DTO.getAddress())
                .userRole(UserRole.of(SIGNUP_REQUEST_DTO.getUserRole()))
                .userStatus(UserStatus.LIVE)
                .build();

        given(userRepository.findByEmailAndUserStatus(LOGIN_REQUEST_DTO.getEmail(), UserStatus.LIVE)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword())).willReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(LOGIN_REQUEST_DTO));
        assertEquals(ErrorCode.LOGIN_FAILED, exception.getErrorCode());
        verify(userRepository).findByEmailAndUserStatus(LOGIN_REQUEST_DTO.getEmail(), UserStatus.LIVE);
        verify(passwordEncoder).matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword());
    }

    @Test
    void 비회원이_로그아웃을_시도하면_예외발생_에러코드_USER_NOT_FOUND() {
        // given
        AuthUser nonUser = new AuthUser(999L, "없는회원임", "없는회원임", null);

        given(userRepository.findByEmail(nonUser.getEmail())).willReturn(Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.logout(nonUser));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findByEmail(nonUser.getEmail());
    }

    @Test
    void 로그인_사용자가아닌_다른회원이_로그아웃을_시도하면_예외발생_에러코드_FORBIDDEN() {
        // given
        Users mockUser = Users.builder()
                .id(1L)
                .email("email@naver.com")
                .nickname("닉네임")
                .password("encodedPassword")
                .phoneNumber("010-1111-2222")
                .address("서울시 강남구")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build();

        AuthUser fakeUser = new AuthUser(999L, mockUser.getEmail(), mockUser.getNickname(), mockUser.getUserRole());

        given(userRepository.findByEmail(fakeUser.getEmail())).willReturn(Optional.of(mockUser));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.logout(fakeUser));
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
        verify(userRepository).findByEmail(fakeUser.getEmail());
    }
}