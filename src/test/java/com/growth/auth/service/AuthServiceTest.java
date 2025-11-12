package com.growth.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
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

// 📌 Question: 회원가입 Service 단위 테스트는 추가적으로 작성할 필요가 없는건지??
// NOTE: Java 단위테스트 특징: DB나 외부 의존성 없이 메모리에서만 실행 + 테스트 대상에만 집중 + Mock 객체 사용
// NOTE: ExtensionWith: Mockito(테스트용 mocking framework) 확장 기능을 사용하여 Mock 객체를 생성하는 어노테이션
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class AuthServiceTest extends UnitTestBase {
  // 1. MemberRepository, passwordEncoder, jwtService Mock 객체 생성
  // 2. AuthService 객체 생성
  // 3. 각 객체 테스트

  // NOTE: Mock 객체 생성을 위한 Mock 어노테이션
  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  // NOTE: JWT Token 생성을 위한 JwtService를 주입받음
  // 테스트에서 jwtService.generateToken() 호출하여 JWT Token 생성 테스트 가능
  @Mock
  private JwtService jwtService;

  // NOTE: Mock 객체 주입을 위한 InjectMocks 어노테이션
  // - 모든 Mock 객체(memberRepository, passwordEncoder, jwtService)를 주입받아 AuthService 객체 생성
  @InjectMocks
  private AuthService authService;

  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  void login_Success() {
    // 1. LoginRequestDto 생성
    // 2. Member 엔티티 생성
    // 3. given() 메서드 사용하여 Mock 객체 동작 정의
    //  3-1. memberRepository.findByEmail(email) 호출하여 Member 엔티티 조회
    //  3-2. passwordEncoder.matches(password, encodedPassword) 호출하여 비밀번호 검증
    //  3-3. jwtService.generateToken(UserIdentity, TokenType.ACCESS) 호출하여 Access Token 생성
    //  3-4. jwtService.generateToken(UserIdentity, TokenType.REFRESH) 호출하여 Refresh Token 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (로그인 성공 시 응답 DTO 반환)
    // 5. assertThat()로 응답 DTO 검증

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

    // String accessToken = "accessToken";
    // String refreshToken = "refreshToken";

    // NOTE: given(): Mock 객체의 동작을 정의하는 메서드
    // - Member 엔티티 조회
    given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
    // - 비밀번호 검증
    given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
    // NOTE: login 메서드에서는 토큰을 생성하지 않음 (토큰은 Controller에서 생성)
    // - jwtService.generateToken은 login 메서드에서 호출되지 않음

    // when
    LoginResponseDto response = authService.login(requestDto);

    // then
    // NOTE: assertThat(): assertj 라이브러리의 메서드 -> 객체의 값을 검증하는 메서드
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email);
    assertThat(response.nickname()).isEqualTo("testuser");
    // NOTE: 토큰은 응답 body에 포함되지 않고 쿠키로만 전달됨
    // assertThat(response.accessToken()).isEqualTo(accessToken);
    // assertThat(response.refreshToken()).isEqualTo(refreshToken);
    // NOTE: 단위 테스트에서는 updatedAt이 null일 수 있음 (실제 저장되지 않기 때문)
    // assertThat(response.lastLoginAt()).isNotNull();

    // NOTE: then(): Mock 객체의 동작을 검증하는 메서드
    // - memberRepository.findByEmail(email) 호출하여 Member 엔티티 조회
    then(memberRepository).should().findByEmail(email);
    // - passwordEncoder.matches(password, encodedPassword) 호출하여 비밀번호 검증
    then(passwordEncoder).should().matches(password, encodedPassword);
    // NOTE: login 메서드에서는 토큰을 생성하지 않음 (토큰은 Controller에서 생성)
    // - jwtService.generateToken은 login 메서드에서 호출되지 않음
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
    assertThatThrownBy(() -> authService.login(requestDto))
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
    assertThatThrownBy(() -> authService.login(requestDto))
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
    assertThatThrownBy(() -> authService.login(requestDto))
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
    assertThatThrownBy(() -> authService.login(requestDto))
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
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");

    then(memberRepository).should().findByEmail(email);
    then(passwordEncoder).should().matches(null, encodedPassword);
  }
}
