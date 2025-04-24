package com.delivery.igo.igo_delivery.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
