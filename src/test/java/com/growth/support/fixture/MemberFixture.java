package com.growth.support.fixture;

import com.growth.member.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberFixture {
  // NOTE: 기본 테스트 회원 생성
  public static Member createMember(PasswordEncoder passwordEncoder) {
    return createMember(
      "test@example.com", "password123", "testuser", passwordEncoder
    );
  }

  // NOTE: 커스터마이징 가능한 회원 생성
  public static Member createMember(String email, String password, String nickname, PasswordEncoder passwordEncoder) {
    String encodedPassword = passwordEncoder.encode(password);
    return Member.builder()
      .email(email).password(encodedPassword).nickname(nickname)
      .build();
  }

  // NOTE: 여러 회원을 한 번에 생성
  public static Member[] createMembers(
    int count, 
    PasswordEncoder passwordEncoder
) {
    Member[] members = new Member[count];
    for (int i = 0; i < count; i++) {
        members[i] = createMember(
            "user" + (i + 1) + "@example.com",
            "password" + (i + 1),
            "user" + (i + 1),
            passwordEncoder
        );
    }
    return members;
}
}
