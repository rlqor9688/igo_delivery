package com.delivery.igo.igo_delivery.api.user.entity;

import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsersUnitTest {

    @Test
    void 회원삭제_검증시_회원의_상태가_INACTIVE이면_예외_발생_에러코드_DELETED_USER() {
        // given
        Users user = Users.builder().userStatus(UserStatus.INACTIVE).build();

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> user.validateDelete());
        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());
    }

    @Test
    void 접근권한_검증시_필드id와_매개변수의_id가_다르면_예외_빨생_에러코드_FORBIDDEN() {
        // given
        Users user = Users.builder().id(1L).build();
        AuthUser authUser = new AuthUser(2L, null, null, null);

        // when * then
        GlobalException exception = assertThrows(GlobalException.class, () -> user.validateAccess(authUser));
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }
}