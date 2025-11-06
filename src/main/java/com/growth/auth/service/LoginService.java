package com.growth.auth.service;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
import com.growth.auth.usecase.LoginUseCase;
import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  public LoginResponseDto login(LoginRequestDto requestDto) {
    // NOTE: 이메일로 회원 조회
    Member member = memberRepository
      .findByEmail(requestDto.email())
      .orElseThrow(() -> new BadRequestException("회원 정보가 없습니다."));

    // NOTE: 비밀번호 검증
    if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
      throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }

    // NOTE: 로그인 성공 시 마지막 로그인 시간 업데이트
    member.updateLastLoginAt();

    // NOTE: UserIdentity 생성
    UserIdentity userIdentity = UserIdentity.from(
      member.getMemberId(),
      member.getEmail()
    );

    // NOTE: JWT Access Token 생성
    String accessToken = jwtService
      .generateToken(userIdentity, TokenType.ACCESS)
      .getValue();

    // NOTE: 로그인 성공 시 응답 DTO 반환 (JWT 토큰 포함)
    return LoginResponseDto.from(member, accessToken);
  }
}
