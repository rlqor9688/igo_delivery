package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmailDuplicatedValidatorUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    EmailDuplicatedValidator validator;

    @Test
    void 중복된_이메일이_아니면_성공() {
        // given
        String email = "test@email.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when & then
        assertTrue(validator.isValid(email, null));
    }

    @Test
    void 중복된_이메일이면_실패() {
        // given
        String email = "test@email.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertFalse(validator.isValid(email, null));
    }
}