package kr.hhplus.be.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST.value(), "INVALID_CHARGE_AMOUNT"),
    BALANCE_EXCEED_MAXIMUM(HttpStatus.BAD_REQUEST.value(), "BALANCE_EXCEED_MAXIMUM"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_BALANCE"),

    OUT_OF_STOCK_COUPON(HttpStatus.BAD_REQUEST.value(), "OUT_OF_STOCK_COUPON"),
    ALREADY_ISSUED_COUPON(HttpStatus.BAD_REQUEST.value(), "ALREADY_ISSUED_COUPON"),
    NOT_STATUS_ISSUED_COUPON(HttpStatus.BAD_REQUEST.value(), "ALREADY_USED_COUPON"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name()),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name()),

    LOCK_ACQUIRED_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LOCK_ACQUIRED_FAILED"),
    INTERNAL_LOCK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_LOCK_ERROR"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name()),
    ;
    private final int code;
    private final String message;

}

