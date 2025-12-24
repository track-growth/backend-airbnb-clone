package com.growth.auth.service;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.dto.response.LoginResultDto;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
import com.growth.auth.usecase.AuthUseCase;
import com.growth.member.domain.Member;
import com.growth.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements AuthUseCase {
  private final MemberAuthService memberAuthService;
  private final JwtService jwtService;

  @Override
  public LoginResultDto login(LoginRequestDto requestDto) {
    // NOTE: Member 도메인에게 인증과 업데이트 책임을 모두 위임
    // NOTE: Auth 도메인은 Member의 내부 구현(Repository, PasswordEncoder 등)을 모름
    // NOTE: 도메인 간 책임 명확히 분리 (Auth: 토큰 발급, Member: 회원 인증)
    Member member = memberAuthService.authenticateAndUpdate(
      requestDto.email(),
      requestDto.password()
    );

    // NOTE: 토큰 생성, 쿠키에 설정 (응답 body에는 포함하지 않음)
    String[] tokens = generateTokens(member);

    // NOTE: 토큰이 포함된 LoginResult 반환
    return new LoginResultDto(LoginResponseDto.from(member), tokens[0], tokens[1]);
  }

  /**
   * 로그인한 사용자의 토큰을 생성
   * @param member 로그인한 회원 정보
   * @return [accessToken, refreshToken]
   */
  public String[] generateTokens(Member member) {
    // NOTE: UserIdentity 생성
    UserIdentity userIdentity = UserIdentity.of(
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
