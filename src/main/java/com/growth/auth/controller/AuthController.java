package com.growth.auth.controller;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.service.AuthService;
import com.growth.auth.util.CookieUtil;
import com.growth.global.common.response.ApiResponse;
import com.growth.member.domain.Member;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final CookieUtil cookieUtil;

  @PostMapping("/api/auth/login")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<LoginResponseDto> login(
    @Valid @RequestBody LoginRequestDto requestDto,
    HttpServletResponse response
  ) {
    // NOTE: 인증 및 마지막 로그인 시간 업데이트
    Member member = authService.authenticateAndUpdateLastLogin(requestDto);
    
    // NOTE: 토큰 생성 및 쿠키에 설정 (응답 body에는 포함하지 않음)
    String[] tokens = authService.generateTokens(member);
    cookieUtil.setAccessTokenCookie(response, tokens[0]);
    cookieUtil.setRefreshTokenCookie(response, tokens[1]);
    
    // NOTE: 응답 DTO 생성 (토큰 제외)
    LoginResponseDto loginResponse = LoginResponseDto.from(member);
    
    return ApiResponse.success(loginResponse);
  }
}
