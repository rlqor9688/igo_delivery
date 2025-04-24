package com.delivery.igo.igo_delivery.common.validation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordValidatorUnitTest {

    PasswordValidator validator = new PasswordValidator();


    @Test
    void 길이가_8자이고_조건에맞으면_성공() {
        assertTrue(validator.isValid("qwer1234!", null));
    }

    @Test
    void 길이가_15자이고_조건에맞으면_성공() {
        assertTrue(validator.isValid("qwert12345!@#$%", null));
    }

    @Test
    void 특수문자가_없으면_실패() {
        assertFalse(validator.isValid("qwert123456", null));
    }

    @Test
    void 숫자가_없으면_실패() {
        assertFalse(validator.isValid("qwert!@#$", null));
    }

    @Test
    void 숫자만_포함된_비밀번호는_실패() {
        assertFalse(validator.isValid("12345678", null));
    }

    @Test
    void 영문만_포함된_비밀번호는_실패() {
        assertFalse(validator.isValid("abcdefg", null));
    }

    @Test
    void 한글이나_공백이_포함되면_실패() {
        assertFalse(validator.isValid("ㅂㅈㄷㄱ12 34!@#$", null));
    }

    @Test
    void 조건에_맞지만_길이가_16자이상이면_실패() {
        assertFalse(validator.isValid("qwert12345!@#$%!", null));
    }

    @Test
    void 조건에_맞지만_길이가_7자이하이면_실패() {
        assertFalse(validator.isValid("qw12!@#", null));
    }

}