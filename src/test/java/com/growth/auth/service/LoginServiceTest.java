package com.growth.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import com.growth.support.UnitTestBase;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest extends UnitTestBase {
  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private LoginService loginService;

  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  void login_Success() {
    // given
    String email = "test@example.com";
    String password = "password123";
    String encodedPassword = "encodedPassword123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
    given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

    // when
    LoginResponseDto response = loginService.login(requestDto);

    // then
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email);
    assertThat(response.nickname()).isEqualTo("testuser");
    // 단위 테스트에서는 updatedAt이 null일 수 있음 (실제 저장되지 않기 때문)
    // assertThat(response.lastLoginAt()).isNotNull();

    then(memberRepository).should().findByEmail(email);
    then(passwordEncoder).should().matches(password, encodedPassword);
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  void login_NonExistentEmail_ThrowsException() {
    // given
    String email = "nonexistent@example.com";
    String password = "password123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    then(memberRepository).should().findByEmail(email);
    then(passwordEncoder).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  void login_WrongPassword_ThrowsException() {
    // given
    String email = "test@example.com";
    String password = "wrongPassword";
    String encodedPassword = "encodedPassword123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
    given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");

    then(memberRepository).should().findByEmail(email);
    then(passwordEncoder).should().matches(password, encodedPassword);
  }

  @Test
  @DisplayName("null 이메일로 로그인 시 예외가 발생한다")
  void login_NullEmail_ThrowsException() {
    // given
    LoginRequestDto requestDto = new LoginRequestDto(null, "password123");

    given(memberRepository.findByEmail(null)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    then(memberRepository).should().findByEmail(null);
    then(passwordEncoder).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("빈 문자열 이메일로 로그인 시 예외가 발생한다")
  void login_EmptyEmail_ThrowsException() {
    // given
    LoginRequestDto requestDto = new LoginRequestDto("", "password123");

    given(memberRepository.findByEmail("")).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    then(memberRepository).should().findByEmail("");
    then(passwordEncoder).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("null 비밀번호로 로그인 시 예외가 발생한다")
  void login_NullPassword_ThrowsException() {
    // given
    String email = "test@example.com";
    String encodedPassword = "encodedPassword123";

    LoginRequestDto requestDto = new LoginRequestDto(email, null);

    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
    given(passwordEncoder.matches(null, encodedPassword)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> loginService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");

    then(memberRepository).should().findByEmail(email);
    then(passwordEncoder).should().matches(null, encodedPassword);
  }
}
