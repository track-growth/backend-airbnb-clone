package com.growth.auth.exception;

/**
 * Auth 도메인에서 반복적으로 사용되는 에러 메시지를 상수로 관리합니다.
 */
public final class AuthErrorMessage {

    private AuthErrorMessage() {
    }

    public static final String UNAUTHORIZED = "인증되지 않은 사용자입니다.";
}

