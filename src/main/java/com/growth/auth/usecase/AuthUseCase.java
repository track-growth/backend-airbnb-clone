package com.growth.auth.usecase;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResultDto;

public interface AuthUseCase {
  LoginResultDto login(LoginRequestDto requestDto);
}
