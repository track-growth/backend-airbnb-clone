package com.growth.member.usecase;

import com.growth.member.dto.request.LoginMemberRequestDto;
import com.growth.member.dto.response.LoginMemberResponseDto;

public interface LoginMemberUseCase {
  LoginMemberResponseDto login(LoginMemberRequestDto requestDto);
}
