package com.delivery.igo.igo_delivery.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    private final int status; //에러 상태 번호
    private final String error; // 에러 원인
    private final String code; // 개발자가 확인하기 편하게 할 에러 코드
    private final String message;  // 사용자에게 보여줄 에러 메세지

    // Review
    ORDER_NOT_FOUND(404, "NOT_FOUND", "R001", "주문을 찾을 수 없습니다."),
    STORE_NOT_FOUND(404, "NOT_FOUND", "R001", "매장 정보를 찾을 수 없습니다.");
}
