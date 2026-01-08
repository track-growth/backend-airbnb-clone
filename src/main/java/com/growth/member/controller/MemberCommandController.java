package com.growth.member.controller;

import com.growth.global.common.response.ApiResponse;
import com.growth.member.dto.request.SignUpMemberRequestDto;
import com.growth.member.dto.response.SignUpMemberResponseDto;
import com.growth.member.usecase.SignUpMemberUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/members") 전체 검색시의 불편함 때문에 저는 사용안하고 있습니다
@RequiredArgsConstructor
public class MemberCommandController {
  private final SignUpMemberUseCase signUpMemberUseCase;

  @PostMapping("/api/members/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<SignUpMemberResponseDto> signUp(
    @Valid @RequestBody SignUpMemberRequestDto requestDto
  ) {
    SignUpMemberResponseDto response = signUpMemberUseCase.signUp(requestDto);
    return ApiResponse.created(response, "회원가입이 완료되었습니다");
  }
}
