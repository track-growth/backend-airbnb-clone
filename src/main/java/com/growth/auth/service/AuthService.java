package com.growth.auth.service;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
import com.growth.auth.usecase.AuthUseCase;
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
public class AuthService implements AuthUseCase {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  public LoginResponseDto login(LoginRequestDto requestDto) {
    Member member = authenticateAndUpdateLastLogin(requestDto);
    return LoginResponseDto.from(member);
  }

  /**
   * 로그인 인증 및 마지막 로그인 시간 업데이트
   * @param requestDto 로그인 요청 DTO
   * @return 인증된 회원 정보
   */
  public Member authenticateAndUpdateLastLogin(LoginRequestDto requestDto) {
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

    return member;
  }

  /**
   * 로그인한 사용자의 토큰을 생성
   * @param member 로그인한 회원 정보
   * @return [accessToken, refreshToken]
   */
  public String[] generateTokens(Member member) {
    // NOTE: UserIdentity 생성
    UserIdentity userIdentity = UserIdentity.from(
      member.getMemberId(),
      member.getEmail()
    );

    // NOTE: JWT Access Token 생성
    String accessToken = jwtService
      .generateToken(userIdentity, TokenType.ACCESS)
      .getValue();

    // NOTE: JWT Refresh Token 생성
    String refreshToken = jwtService
      .generateToken(userIdentity, TokenType.REFRESH)
      .getValue();

    // NOTE: Response DTO를 리턴하는 게 아니라, [accessToken, refreshToken] 배열을 리턴
    // - 토큰은 쿠키로만 전달되므로, Response Body에는 포함하지 않음
    return new String[]{accessToken, refreshToken};
  }
}
