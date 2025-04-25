package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.common.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    /**
     * 영어(대,소문자), 숫자, 특수문자로만 이루어진 8 ~ 15 길이만 비밀번호 검증 통과
     */
    private static final String REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(REGEX);
    }
}