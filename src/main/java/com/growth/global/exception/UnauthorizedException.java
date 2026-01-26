package com.growth.global.exception;

/**
 * 인증되지 않은 사용자 접근 등, 인증이 필요한 상황에서 사용하는 예외입니다.
 * 전역 예외 처리에서 HTTP 401(UNAUTHORIZED)로 매핑됩니다.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

