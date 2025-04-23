package com.delivery.igo.igo_delivery.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 인증 실패 시 발생하는 예외 응답
     *
     * @param e AuthException
     * @param request 서블릿 요청
     * @return ErrorDto, 상태코드
     */
    @ExceptionHandler
    public ResponseEntity<ErrorDto> authFailedException(AuthException e, HttpServletRequest request) {
        log.error("[authFiledException] ex: ", e);
        ErrorCode errorCode = e.getErrorCode();

        ErrorDto errorDto = new ErrorDto(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());

        return new ResponseEntity<>(errorDto, errorCode.getHttpStatus());
    }

    /**
     * 입력값 검증 예외 응답(빈 벨리데이션 예외시)
     *
     * @param e Bean Validation 예외
     * @param request 서블릿
     * @return ErrorDto, 400 상태코드
     */
    @ExceptionHandler
    public ResponseEntity<ErrorDto> inputValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("[inputValidException] ex: ", e);

        Map<String, List<String>> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ErrorDto errorDto = new ErrorDto(
                ErrorCode.VALID_BAD_REQUEST.getHttpStatus().value(),
                errors.toString(),
                LocalDateTime.now(),
                request.getRequestURI());
        return new ResponseEntity<>(errorDto, ErrorCode.VALID_BAD_REQUEST.getHttpStatus());
    }
}
