package com.growth.member.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Member 도메인의 인증 관련 책임을 처리하는 서비스
 * Auth 도메인이 Member의 내부 구현(Repository, 비밀번호 검증 등)을 알 필요 없이,
 * 이 서비스를 통해 Member 도메인과 소통합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemberAuthService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final Clock clock;

  /**
   * 이메일과 비밀번호로 회원을 인증하고, 로그인 성공 시 필요한 정보를 업데이트합니다.
   * Member 도메인의 모든 책임(조회, 검증, 업데이트)을 캡슐화합니다.
   *
   * @param email 로그인할 이메일
   * @param password 로그인할 비밀번호 (평문)
   * @return 인증되고 업데이트된 회원 정보
   * @throws BadRequestException 회원 정보가 없거나 비밀번호가 일치하지 않는 경우
   */
  public Member authenticateAndUpdate(String email, String password) {
    // NOTE: Member 조회 - Member 도메인의 책임
    Member member = findMemberByEmail(email);

    // NOTE: 비밀번호 검증 - Member 도메인의 책임
    validatePassword(password, member.getPassword());

    // NOTE: 로그인 성공 업데이트 - Member 도메인이 결정
    // Member 엔티티는 MemberUpdate 인터페이스를 구현하여 자신의 업데이트 로직을 관리
    member.onLoginSuccess(clock);

    // NOTE: 영속성 컨텍스트에서 관리되는 엔티티 반환
    // Dirty Checking에 의해 트랜잭션 커밋 시 자동으로 UPDATE 쿼리 실행
    return member;
  }

  /**
   * 이메일로 회원을 조회합니다.
   *
   * @param email 조회할 이메일
   * @return 조회된 회원
   * @throws BadRequestException 회원이 존재하지 않는 경우
   */
  private Member findMemberByEmail(String email) {
    return memberRepository
      .findByEmail(email)
      .orElseThrow(() -> new BadRequestException("회원 정보가 없습니다."));
  }

  /**
   * 입력된 비밀번호와 저장된 비밀번호를 검증합니다.
   *
   * @param rawPassword 평문 비밀번호
   * @param encodedPassword 암호화된 비밀번호
   * @throws BadRequestException 비밀번호가 일치하지 않는 경우
   */
  private void validatePassword(String rawPassword, String encodedPassword) {
    if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }
  }

  /**
   * 회원 존재 여부를 검증합니다.
   * 다른 도메인에서 Member의 존재 여부를 확인할 때 사용됩니다.
   * (예: Room 생성 시 호스트 검증, Reservation 생성 시 게스트 검증)
   *
   * @param memberId 검증할 회원 ID
   * @throws BadRequestException 회원이 존재하지 않는 경우
   */
  public void validateMemberExists(java.util.UUID memberId) {
    if (!memberRepository.existsById(memberId)) {
      throw new BadRequestException("존재하지 않는 회원입니다.");
    }
  }
}

