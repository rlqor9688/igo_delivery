package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
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

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void 내정보가_정상적으로_조회됨() {
        // given
        Users user = Users.builder()
                .id(1L)
                .email("email@naver.com")
                .nickname("정상유저")
                .phoneNumber("010-1111-2222")
                .address("세상에없는구")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();

        AuthUser authUser = new AuthUser(1L, "email@naver.com", "정상유저", UserRole.OWNER);
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
    void 내정보가_없으면_예외_발생_에러코드_USER_NOT_FOUND() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@naver.com", "로그인유저", UserRole.OWNER);
        given(userRepository.findById(authUser.getId())).willReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> userService.findUserById(1L, authUser));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(authUser.getId());

    }
}

