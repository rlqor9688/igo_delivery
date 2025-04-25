package com.delivery.igo.igo_delivery.common.exception;

public class ReviewException extends RuntimeException {

    public ReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        }
}
