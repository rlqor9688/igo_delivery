package com.delivery.igo.igo_delivery.common.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderUnitTest {

    PasswordEncoder encoder = new PasswordEncoder();

    @Test
    void 비밀번호_인코딩_성공() {
        // given
        String rawPassword = "qwer1234!";

        // when
        String encoded = encoder.encode(rawPassword);

        // then
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
    }

    @Test
    void 동일한_비밀번호는_매칭_성공() {
        // given
        String rawPassword = "qwer1234!";
        String inputPassword = "qwer1234!";

        // when
        String encoded = encoder.encode(rawPassword);

        // then
        assertTrue(encoder.matches(inputPassword, encoded));
    }

    @Test
    void 다른_비밀번호는_매칭_실패() {
        // given
        String rawPassword = "qwer1234!";
        String wrongPassword = "wrong1234!";

        // when
        String encoded = encoder.encode(rawPassword);

        // then
        assertFalse(encoder.matches(wrongPassword, encoded));
    }

}