package com.growth.member.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.dto.request.LoginMemberRequestDto;
import com.growth.member.dto.response.LoginMemberResponseDto;
import com.growth.member.repository.MemberJpaRepository;
import com.growth.member.usecase.LoginMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginMemberService implements LoginMemberUseCase {
  private final MemberJpaRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public LoginMemberResponseDto login(LoginMemberRequestDto requestDto) {
    // NOTE: 이메일로 회원 조회
    Member member = memberRepository
      .findByEmail(requestDto.email())
      .orElseThrow(() -> new BadRequestException("회원 정보가 없습니다."));

    // NOTE: 비밀번호 검증
    if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
      throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }

    // NOTE: 로그인 성공 시 응답 DTO 반환
    return LoginMemberResponseDto.from(member);
  }
}
