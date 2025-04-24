package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.annotation.NicknameDuplicate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NicknameDuplicatedValidator implements ConstraintValidator<NicknameDuplicate, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        return nickname != null && !userRepository.existsByNickname(nickname);
    }
}