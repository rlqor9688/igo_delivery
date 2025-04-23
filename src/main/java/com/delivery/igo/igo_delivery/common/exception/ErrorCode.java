package com.delivery.igo.igo_delivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예외 코드 모음 - Enum
 */
@Getter
public enum ErrorCode {

    // Common -> default 용도
    DUPLICATED(HttpStatus.BAD_REQUEST, "중복 되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),

    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패, 아이디나 비밀번호를 확인해 주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다. 로그인 후 시도해 주세요."),
    LOGIN_FAILED_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호를 확인해 주세요."),

    // Valid
    VALID_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 입력값 입니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),

    // User
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "사용자 이름이 중복되었습니다. 다른 이름으로 가입해 주세요."),
    DUPLICATED_USER(HttpStatus.BAD_REQUEST, "사용자 이름이나 email이 이미 등록되어있습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // CartItem
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}