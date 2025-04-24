package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public Users validateOwner(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.OWNER) {
            throw new GlobalException(ErrorCode.ROLE_OWNER_FORBIDDEN);
        }

        return user;
    }
}
