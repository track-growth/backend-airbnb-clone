package com.growth.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import com.growth.support.IntegrationTestBase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("LoginService 통합 테스트")
class LoginServiceIntegrationTest extends IntegrationTestBase {
  @Autowired
  private LoginService loginService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  void login_Success() {
    // given
    String email = "test@example.com";
    String password = "password123";
    String encodedPassword = passwordEncoder.encode(password);

    // 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(member);

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // when
    LoginResponseDto response = loginService.login(requestDto);

    // then
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email);
    assertThat(response.nickname()).isEqualTo("testuser");
    assertThat(response.lastLoginAt()).isNotNull();
    assertThat(response.lastLoginAt()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  void login_NotExistentEmail_ThrowsException() {
    // given
    String email = "testNotExist@example.com";
    String password = "test123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  void login_WrongPassword_ThrowsException() {
    // given
    String email = "test@example.com";
    String correctPassword = "123";
    String wrongPassword = "wrongPassword";
    String encodedPassword = passwordEncoder.encode(correctPassword);

    // 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(member);

    LoginRequestDto requestDto = new LoginRequestDto(email, wrongPassword);

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");
  }

  @Test
  @DisplayName("여러 회원이 있을 때 특정 회원으로 로그인할 수 있다")
  void login_WithMultipleMembers_Success() {
    // given - 여러 회원 생성
    String email1 = "user1@example.com";
    String email2 = "user2@example.com";
    String password1 = "password1";
    String password2 = "password2";

    Member member1 = Member
      .builder()
      .email(email1)
      .password(passwordEncoder.encode(password1))
      .nickname("user1")
      .build();

    Member member2 = Member
      .builder()
      .email(email2)
      .password(passwordEncoder.encode(password2))
      .nickname("user2")
      .build();

    memberRepository.save(member1);
    memberRepository.save(member2);

    // when - 첫 번째 회원으로 로그인
    LoginRequestDto requestDto = new LoginRequestDto(email1, password1);

    LoginResponseDto response = loginService.login(requestDto);

    // then
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email1);
    assertThat(response.nickname()).isEqualTo("user1");
  }

  @Test
  @DisplayName("대소문자가 다른 이메일로 로그인 시 예외가 발생한다")
  void login_CaseSensitiveEmail_ThrowsException() {
    // given
    String email = "test@example.com";
    String differentCaseEmail = "TEST@EXAMPLE.COM";
    String password = "password123";
    String encodedPassword = passwordEncoder.encode(password);

    // 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(member);

    LoginRequestDto requestDto = new LoginRequestDto(
      differentCaseEmail,
      password
    );

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("공백이 포함된 이메일로 로그인 시 예외가 발생한다")
  void login_EmailWithWhitespace_ThrowsException() {
    // given
    String email = "test@example.com";
    String emailWithWhitespace = " test@example.com ";
    String password = "password123";
    String encodedPassword = passwordEncoder.encode(password);

    // 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(member);

    LoginRequestDto requestDto = new LoginRequestDto(
      emailWithWhitespace,
      password
    );

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("공백이 포함된 비밀번호로 로그인 시 예외가 발생한다")
  void login_PasswordWithWhitespace_ThrowsException() {
    // given
    String email = "test@example.com";
    String password = "password123";
    String passwordWithWhitespace = " password123 ";
    String encodedPassword = passwordEncoder.encode(password);

    // 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(member);

    LoginRequestDto requestDto = new LoginRequestDto(
      email,
      passwordWithWhitespace
    );

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");
  }
}
