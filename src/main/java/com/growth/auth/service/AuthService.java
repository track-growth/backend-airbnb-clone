package com.growth.auth.service;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.dto.response.LoginResultDto;
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
import java.time.Clock;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements AuthUseCase {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final Clock clock;

  @Override
  public LoginResultDto login(LoginRequestDto requestDto) {
    // NOTE: 로그인 인증 및 마지막 로그인 시간 업데이트
    Member member = authenticateAndUpdateLastLogin(requestDto);

    // NOTE: 토큰 생성, 쿠키에 설정 (응답 body에는 포함하지 않음)
    String[] tokens = generateTokens(member);

    // NOTE: 토큰이 포함된 LoginResult 반환
    return new LoginResultDto(LoginResponseDto.from(member), tokens[0], tokens[1]);
  }

  /**
   * 로그인 인증 및 마지막 로그인 시간 업데이트
   * @param requestDto 로그인 요청 DTO
   * @return 인증된 회원 정보
   */
  public Member authenticateAndUpdateLastLogin(LoginRequestDto requestDto) {
    // NOTE: 이메일로 DB에서 Member 엔티티 조회 -> 조회된 엔티티는 Persistence Context에 관리됨
    // NOTE: JPA는 Member 엔티티의 Snapshot을 Persistence Context에 보관
    Member member = memberRepository
      .findByEmail(requestDto.email())
      .orElseThrow(() -> new BadRequestException("회원 정보가 없습니다."));

    // NOTE: 비밀번호 검증
    if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
      // NOTE: 비밀번호가 일치하지 않으면 예외 발생 -> 모든 작업 롤백(트랜잭션 롤백)
      throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }

    // NOTE: 로그인 성공 시 마지막 로그인 시간 업데이트
    // NOTE: member 엔티티 상태 변경 (Persistence Context에 관리되고 있는 엔티티의 상태를 변경 -> lastLoginAt 필드 값 업데이트되면 변경을 감지)
    // - JPA의 변경 감지(Dirty Checking) 기능이 자동으로 동작 -> 트랜잭션 커밋 시점에 변경된 필드를 자동으로 감지하고, 자동으로 UPDATE 쿼리 실행되어 DB에 반영됨
    member.updateLastLoginAt(clock);

    // NOTE: member는 이미 영속 상태에 해당되고, 변경 감지가 자동으로 처리되므로 save() 호출은 불필요함
    // NOTE: save()가 필요한 경우: 새로운 엔티티를 저장할 때 (비영속 -> 영속)
    // memberRepository.save(member);

    // NOTE: Persistence Context에 관리된 엔티티 반환
    return member;
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
