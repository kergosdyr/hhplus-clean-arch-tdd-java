package com.justin.clean.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {
    DEFAULT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),

    DUPLICATE_REGISTER_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "이미 수강신청 완료된 특강입니다", LogLevel.ERROR),

    REGISTER_OVER_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "수강신청이 이미 마감된 특강입니다.", LogLevel.ERROR);

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {

        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }
}
