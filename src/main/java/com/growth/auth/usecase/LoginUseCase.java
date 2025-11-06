package com.growth.auth.usecase;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;

public interface LoginUseCase {
  LoginResponseDto login(LoginRequestDto requestDto);
}
