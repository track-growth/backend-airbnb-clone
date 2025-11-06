package com.growth.auth.controller;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.usecase.LoginUseCase;
import com.growth.global.common.response.ApiResponse;
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
  private final LoginUseCase loginUseCase;

  @PostMapping("/api/auth/login")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<LoginResponseDto> login(
    @Valid @RequestBody LoginRequestDto requestDto
  ) {
    LoginResponseDto response = loginUseCase.login(requestDto);
    return ApiResponse.success(response);
  }
}
