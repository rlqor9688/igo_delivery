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
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청값 입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류 발생, 확인 후 조치하겠습니다."),


    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패, 아이디나 비밀번호를 확인해 주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다. 로그인 후 시도해 주세요."),
    USER_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    USER_EXIST_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    AUTH_TYPE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),

    // password
    PASSWORD_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 비밀번호로 중복 요청할 수 없습니다"),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "기존 비밀번호가 다릅니다."),

    // JWT
    JWT_REQUIRED(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다."),
    JWT_BAD_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    JWT_INVALID_TOKEN( HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 토큰입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    JWT_NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "JWT 토큰이 없습니다"),

    // Valid
    VALID_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 입력값 입니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    COMPLETE_ORDER(HttpStatus.BAD_REQUEST,"이미 완료된 주문입니다."),
    CANCELLED_ORDER(HttpStatus.BAD_REQUEST,"이미 취소된 주문입니다."),
    REFUSED_ORDER(HttpStatus.BAD_REQUEST,"이미 거부된 주문입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST,"존재하지 않는 주문 상태입니다."),
    OUT_OF_OPEN_TIME(HttpStatus.FORBIDDEN, "영업시간이 아닙니다."),
    UNDER_MIN_ORDER_PRICE(HttpStatus.BAD_REQUEST, "최소 주문 금액보다 낮습니다."),
    CONSUMER_CANNOT_CHANGE_STATUS(HttpStatus.FORBIDDEN, "고객은 주문 취소 외에는 상태를 변경할 수 없습니다."),
    OWNER_CANNOT_CANCEL_ORDER(HttpStatus.FORBIDDEN, "매장 주인은 주문을 취소할 수 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.NOT_FOUND, "삭제된 사용자 입니다."),
    USER_NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "권한 정보가 없습니다."),
    INVALID_USER_ROLE(HttpStatus.FORBIDDEN, "유효하지 않은 사용자 권한입니다."),
    ROLE_ADMIN_FORBIDDEN(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),
    ROLE_CONSUMER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "주문 고객이 아닙니다."),
    ROLE_OWNER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "매장 사장님이 아닙니다."),

    // CartItem, Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 담긴 상품을 찾을 수 없습니다."),

    // Store
    NOT_OWNER(HttpStatus.FORBIDDEN, "사장님 권한이 아닙니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
    STORE_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "해당 가게의 사장님만 접근할 수 있습니다."),
    MAX_STORE_LIMIT(HttpStatus.BAD_REQUEST, "매장은 최대 3개까지 생성할 수 있습니다."),

    // Menu
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    DELETED_MENU(HttpStatus.NOT_FOUND, "삭제된 메뉴입니다."),

    // Review
    REVIEW_USER_MISMATCH(HttpStatus.FORBIDDEN, "본인의 주문에만 리뷰를 남길 수 있습니다."),
    REVIEW_STORE_MISMATCH(HttpStatus.FORBIDDEN, "주문한 가게 정보와 다릅니다."),
    REVIEW_ORDERITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 아이템을 찾을 수 없습니다."),
    REVIEW_ORDER_INVALID(HttpStatus.BAD_REQUEST, "주문이 완료된 건에만 리뷰를 남길 수 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "삭제된 리뷰는 수정할 수 없습니다." ),
    REVIEW_STORE_IS_CLOSED(HttpStatus.BAD_REQUEST, "삭제된 매장입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}