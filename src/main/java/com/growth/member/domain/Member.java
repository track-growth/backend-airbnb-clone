package com.growth.member.domain;

import com.growth.global.common.entity.BaseEntity;
import com.growth.member.dto.request.SignUpMemberRequestDto;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Clock;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
  @Id
  @Column(name = "member_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID memberId;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  public static Member from(
    SignUpMemberRequestDto requestDto,
    String encryptedPassword
  ) {
    return Member
      .builder()
      .email(requestDto.email())
      .password(encryptedPassword)
      .nickname(requestDto.nickname())
      .build();
  }

  @Builder
  private Member(
    String email,
    String password,
    String nickname,
    LocalDateTime lastLoginAt
  ) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.lastLoginAt = lastLoginAt;
  }

  public void updateLastLoginAt(Clock clock) {
    // NOTE: clock을 사용하여 마지막 로그인 시간 업데이트
    this.lastLoginAt = LocalDateTime.now(clock);
  }
}
