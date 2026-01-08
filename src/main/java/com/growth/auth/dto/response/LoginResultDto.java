package com.growth.auth.dto.response;

/**
 * NOTE: 로그인 이후 프레젠테이션 계층에서 필요한 데이터를 묶어 전달
 *
 * @param loginResponseDto 응답 본문으로 내려갈 DTO
 * @param accessToken 쿠키/헤더에 기록할 JWT Access Token
 * @param refreshToken 쿠키/헤더에 기록할 JWT Refresh Token
 */
public record LoginResultDto(
  LoginResponseDto loginResponseDto,
  String accessToken,
  String refreshToken
) {}

