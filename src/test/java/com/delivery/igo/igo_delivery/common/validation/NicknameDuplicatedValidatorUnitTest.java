package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NicknameDuplicatedValidatorUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    NicknameDuplicatedValidator validator;

    @Test
    void 중복된_닉네임이_아니면_성공() {
        // given
        String nickname = "내가닉네임이다";
        given(userRepository.existsByNickname(nickname)).willReturn(false);

        // when & then
        assertTrue(validator.isValid(nickname, null));
    }

    @Test
    void 중복된_닉네임이면_실패() {
        // given
        String nickname = "내가닉네임이다";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // when & then
        assertFalse(validator.isValid(nickname, null));
    }
}