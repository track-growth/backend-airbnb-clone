package com.growth.auth.usecase;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;

public interface AuthUseCase {
  LoginResponseDto login(LoginRequestDto requestDto);
}
