package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.IgoDeliveryApplication;
import com.delivery.igo.igo_delivery.api.user.dto.request.DeleteUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdatePasswordRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.config.PasswordEncoder;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = IgoDeliveryApplication.class)
@Transactional
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    Users user;
    AuthUser authUser;

    @BeforeEach
    void testInit() {
        user = Users.builder()
                .email("email@naver.com")
                .nickname("정상유저")
                .phoneNumber("010-1111-2222")
                .password(passwordEncoder.encode("oldPassword123!@#"))
                .address("세상에없는구")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();

        userRepository.save(user);

        authUser = new AuthUser(user.getId(), "email@naver.com", "정상유저", UserRole.OWNER);
    }

    @Test
    void 유저_정보_수정_서비스_리포지토리_통합테스트_성공() {
        // given
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                "수정할게요",
                "010-9999-9999",
                "수정된주소",
                UserRole.CONSUMER);

        // when
        UserResponseDto userResponseDto = userService.updateUserById(user.getId(), authUser, requestDto);

        // then - 요청값과 저장된 값이 일치함
        assertEquals(requestDto.getNickname(), userResponseDto.getNickname());
        assertEquals(requestDto.getPhoneNumber(), userResponseDto.getPhoneNumber());
        assertEquals(requestDto.getAddress(), userResponseDto.getAddress());
        assertEquals(requestDto.getRole().toString(), userResponseDto.getRole());

    }

    @Test
    void 유저_비밀번호_수정_서비스_리포지토리_통합테스트_성공() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("oldPassword123!@#", "newPassword123!@#");

        // when
        userService.updateUserPasswordById(user.getId(), authUser, requestDto);
        Users findUsers = userRepository.findById(user.getId()).get();

        // then
        assertTrue(passwordEncoder.matches(requestDto.getNewPassword(), findUsers.getPassword()));
        assertFalse(passwordEncoder.matches(requestDto.getOldPassword(), findUsers.getPassword()));
    }

    @Test
    void 유저_비밀번호_수정이_실패하면_성공적으로_트랜잭션이_롤백_되어야함() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("oldPassword123!@#", "newPassword123!@#");
        AuthUser otherAuthUser = new AuthUser(999L, "other@naver.com", "다른유저", UserRole.OWNER);

        // when & then
        assertThrows(GlobalException.class,
                () -> userService.updateUserPasswordById(user.getId(), otherAuthUser, requestDto)); // 인증 유저와 비밀번호 변경 유저가 다름
        Users findUsers = userRepository.findById(user.getId()).get();

        assertTrue(passwordEncoder.matches(requestDto.getOldPassword(), findUsers.getPassword()));  // 기존 비밀번호로 검증이 되어야함
        assertFalse(passwordEncoder.matches(requestDto.getNewPassword(), findUsers.getPassword())); // 바꾸려하는 비밀번호로 검증이 안됨

    }

    @Test
    void 비밀번호_수정이_정상적으로_성공() {
        // given
        DeleteUserRequestDto requestDto = new DeleteUserRequestDto("oldPassword123!@#");

        // when
        userService.deleteUser(user.getId(), authUser, requestDto);

        // then
        assertEquals(UserStatus.INACTIVE, user.getUserStatus());
    }

    @Test
    void 비밀번호_수정이_실패하면_트랜잭션롤백() {
        // given
        DeleteUserRequestDto requestDto = new DeleteUserRequestDto("oldPassword123!@#");
        AuthUser otherAuthUser = new AuthUser(999L, "other@naver.com", "다른유저", UserRole.OWNER);

        // when & then
        assertThrows(GlobalException.class, () -> userService.deleteUser(user.getId(), otherAuthUser, requestDto));
        assertEquals(UserStatus.LIVE, user.getUserStatus());
        assertNotEquals(UserStatus.INACTIVE, user.getUserStatus());
    }
}