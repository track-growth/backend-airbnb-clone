package com.growth.auth.controller;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.dto.response.LoginResultDto;
import com.growth.auth.service.AuthService;
import com.growth.auth.util.CookieUtil;
import com.growth.global.common.response.ApiResponse;
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
    LoginResultDto loginResult = authService.login(requestDto);

    cookieUtil.setTokenCookies(response, loginResult.accessToken(), loginResult.refreshToken());

    return ApiResponse.success(loginResult.loginResponseDto());
  }
}
