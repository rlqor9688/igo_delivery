package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.request.UpdatePasswordRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private Users user;
    private AuthUser authUser;

    @BeforeEach
    void testInit() {
        user = Users.builder()
                .id(1L)
                .email("email@naver.com")
                .nickname("정상유저")
                .phoneNumber("010-1111-2222")
                .address("세상에없는구")
                .password("encodedPassword")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();

        authUser = new AuthUser(1L, "email@naver.com", "정상유저", UserRole.OWNER);
    }

    @Test
    void 내정보가_정상적으로_조회됨() {
        // given
        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));

        // when
        UserResponseDto findUserDto = userService.findUserById(1L, authUser);

        // then
        assertEquals(user.getId(), findUserDto.getId());
        assertEquals(user.getEmail(), findUserDto.getEmail());
        assertEquals(user.getNickname(), findUserDto.getNickname());
        verify(userRepository).findById(authUser.getId());
    }

    @Test
    void 내정보_조회시_내정보가_없으면_예외_발생_에러코드_USER_NOT_FOUND() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@naver.com", "정상유저", UserRole.OWNER);

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.findUserById(1L, authUser));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(authUser.getId());

    }

    @Test
    void 내정보_수정시_변경할_닉네임이_이미있다면_에러_발생_에러코드_USER_EXIST_NICKNAME() {
        // given
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("수정할게요",
                "010-1111-1111",
                "주소",
                UserRole.CONSUMER);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(userRepository.existsByNickname(requestDto.getNickname())).willReturn(true);

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userService.updateUserById(1L, authUser, requestDto));
        assertEquals(ErrorCode.USER_EXIST_NICKNAME, exception.getErrorCode());
        verify(userRepository).findById(authUser.getId());
        verify(userRepository).existsByNickname(requestDto.getNickname());

    }

    @Test
    void 기존_비밀번호와_변경할_비밀번호가_동일하게_요청이오면_예외_발생_ErrorCode_PASSWORD_DUPLICATED() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("samePassword", "samePassword");

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userService.updateUserPasswordById(1L, authUser, requestDto));
        assertEquals(ErrorCode.PASSWORD_DUPLICATED, exception.getErrorCode());

    }

    @Test
    void 입력한_비밀번호가_DB에서_조회된_비밀번호를_매칭할때_다르면_예외_발생_ErrorCode_PASSWORD_NOT_MATCHED() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("oldPassword", "newPassword");

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(false);

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userService.updateUserPasswordById(1L, authUser, requestDto));
        assertEquals(ErrorCode.PASSWORD_NOT_MATCHED, exception.getErrorCode());

    }
}

