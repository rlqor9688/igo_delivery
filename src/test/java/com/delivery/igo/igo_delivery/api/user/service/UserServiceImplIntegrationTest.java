package com.delivery.igo.igo_delivery.api.user.service;

import com.delivery.igo.igo_delivery.IgoDeliveryApplication;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IgoDeliveryApplication.class)
@Transactional
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserServiceImpl userService;

    @Test
    void 유저_정보_수정_서비스_리포지토리_통합테스트_성공() {
        // given
        Users user = Users.builder()
                .email("email@naver.com")
                .nickname("정상유저")
                .phoneNumber("010-1111-2222")
                .password("encodedPassword")
                .address("세상에없는구")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build();
        userRepository.save(user);

        AuthUser authUser = new AuthUser(1L, "email@naver.com", "정상유저", UserRole.OWNER);
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                "수정할게요",
                "010-9999-9999",
                "수정된주소",
                UserRole.CONSUMER);

        // when
        UserResponseDto userResponseDto = userService.updateUserById(1L, authUser, requestDto);

        // then
        // 요청값과 저장된 값이 일치함
        assertEquals(requestDto.getNickname(), userResponseDto.getNickname());
        assertEquals(requestDto.getPhoneNumber(), userResponseDto.getPhoneNumber());
        assertEquals(requestDto.getAddress(), userResponseDto.getAddress());
        assertEquals(requestDto.getRole().toString(), userResponseDto.getRole().toString());

    }

}