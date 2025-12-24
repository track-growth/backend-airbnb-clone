package com.growth.member.domain;

import java.time.Clock;

/**
 * Member 도메인의 업데이트 책임을 정의하는 인터페이스
 * 다른 도메인(예: Auth)이 Member의 구체적인 업데이트 방법을 알 필요 없이,
 * "무엇을 해야 하는지(what)"만 알려주면 Member가 "어떻게 처리할지(how)"를 결정합니다.
 */
public interface MemberUpdate {
  /**
   * 로그인 성공 시 필요한 Member 정보를 업데이트합니다.
   * 구체적인 업데이트 내용(lastLoginAt, loginCount 등)은
   * Member 도메인이 결정하며, 외부에서는 이 메서드만 호출하면 됩니다.
   *
   * @param clock 시간 정보를 제공하는 Clock 객체 (테스트 가능하도록 주입)
   */
  void onLoginSuccess(Clock clock);
}
