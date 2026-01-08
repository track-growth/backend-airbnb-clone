package com.growth.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.dto.response.LoginResultDto;
import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.service.MemberAuthService;
import com.growth.support.UnitTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// 📌 Question: 회원가입 Service 단위 테스트는 추가적으로 작성할 필요가 없는건지??
// NOTE: Java 단위테스트 특징: DB나 외부 의존성 없이 메모리에서만 실행 + 테스트 대상에만 집중 + Mock 객체 사용
// NOTE: ExtensionWith: Mockito(테스트용 mocking framework) 확장 기능을 사용하여 Mock 객체를 생성하는 어노테이션
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class AuthServiceTest extends UnitTestBase {
  // NOTE: AuthService는 MemberAuthService와 JwtService만 의존
  // NOTE: Member 관련 책임(조회, 인증, 업데이트)은 MemberAuthService가 담당

  // NOTE: MemberAuthService Mock 객체 생성
  // Member 도메인의 인증 책임을 가진 서비스
  @Mock
  private MemberAuthService memberAuthService;

  // NOTE: JWT Token 생성을 위한 JwtService를 주입받음
  // 테스트에서 jwtService.generateToken() 호출하여 JWT Token 생성 테스트 가능
  @Mock
  private JwtService jwtService;

  // NOTE: Mock 객체 주입을 위한 InjectMocks 어노테이션
  // - 모든 Mock 객체(memberAuthService, jwtService)를 주입받아 AuthService 객체 생성
  @InjectMocks
  private AuthService authService;

  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  void login_Success() {
    // 1. LoginRequestDto 생성
    // 2. Member 엔티티 생성
    // 3. given() 메서드 사용하여 Mock 객체 동작 정의
    //  3-1. memberAuthService.authenticateAndUpdate(email, password) 호출하여 인증된 Member 반환
    //  3-2. jwtService.generateToken(UserIdentity, TokenType.ACCESS) 호출하여 Access Token 생성
    //  3-3. jwtService.generateToken(UserIdentity, TokenType.REFRESH) 호출하여 Refresh Token 생성
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

    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    EncodedToken encodedAccessToken = EncodedToken.from(accessToken);
    EncodedToken encodedRefreshToken = EncodedToken.from(refreshToken);

    // NOTE: given(): Mock 객체의 동작을 정의하는 메서드
    // NOTE: MemberAuthService가 Member 도메인의 인증 책임을 모두 처리
    given(memberAuthService.authenticateAndUpdate(email, password)).willReturn(member);
    given(jwtService.generateToken(any(UserIdentity.class), eq(TokenType.ACCESS))).willReturn(encodedAccessToken);
    given(jwtService.generateToken(any(UserIdentity.class), eq(TokenType.REFRESH))).willReturn(encodedRefreshToken);

    // when
    LoginResultDto loginResult = authService.login(requestDto);
    LoginResponseDto response = loginResult.loginResponseDto();

    // then
    // NOTE: assertThat(): assertj 라이브러리의 메서드 -> 객체의 값을 검증하는 메서드
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email);
    assertThat(response.nickname()).isEqualTo("testuser");
    assertThat(loginResult.accessToken()).isEqualTo(accessToken);
    assertThat(loginResult.refreshToken()).isEqualTo(refreshToken);

    // NOTE: then(): Mock 객체의 동작을 검증하는 메서드
    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate(email, password);
    then(jwtService).should().generateToken(any(UserIdentity.class), eq(TokenType.ACCESS));
    then(jwtService).should().generateToken(any(UserIdentity.class), eq(TokenType.REFRESH));
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  void login_NonExistentEmail_ThrowsException() {
    // given
    String email = "nonexistent@example.com";
    String password = "password123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // NOTE: MemberAuthService가 예외를 발생시킴 (Member 도메인의 책임)
    given(memberAuthService.authenticateAndUpdate(email, password))
      .willThrow(new BadRequestException("회원 정보가 없습니다."));

    // when & then
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate(email, password);
    then(jwtService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  void login_WrongPassword_ThrowsException() {
    // given
    String email = "test@example.com";
    String password = "wrongPassword";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // NOTE: MemberAuthService가 비밀번호 검증 후 예외를 발생시킴 (Member 도메인의 책임)
    given(memberAuthService.authenticateAndUpdate(email, password))
      .willThrow(new BadRequestException("비밀번호가 일치하지 않습니다."));

    // when & then
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");

    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate(email, password);
    then(jwtService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("null 이메일로 로그인 시 예외가 발생한다")
  void login_NullEmail_ThrowsException() {
    // given
    LoginRequestDto requestDto = new LoginRequestDto(null, "password123");

    // NOTE: MemberAuthService가 null 이메일에 대한 예외를 발생시킴
    given(memberAuthService.authenticateAndUpdate(null, "password123"))
      .willThrow(new BadRequestException("회원 정보가 없습니다."));

    // when & then
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate(null, "password123");
    then(jwtService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("빈 문자열 이메일로 로그인 시 예외가 발생한다")
  void login_EmptyEmail_ThrowsException() {
    // given
    LoginRequestDto requestDto = new LoginRequestDto("", "password123");

    // NOTE: MemberAuthService가 빈 이메일에 대한 예외를 발생시킴
    given(memberAuthService.authenticateAndUpdate("", "password123"))
      .willThrow(new BadRequestException("회원 정보가 없습니다."));

    // when & then
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");

    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate("", "password123");
    then(jwtService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("null 비밀번호로 로그인 시 예외가 발생한다")
  void login_NullPassword_ThrowsException() {
    // given
    String email = "test@example.com";

    LoginRequestDto requestDto = new LoginRequestDto(email, null);

    // NOTE: MemberAuthService가 null 비밀번호에 대한 예외를 발생시킴
    given(memberAuthService.authenticateAndUpdate(email, null))
      .willThrow(new BadRequestException("비밀번호가 일치하지 않습니다."));

    // when & then
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");

    // NOTE: AuthService는 MemberAuthService에게만 의존
    then(memberAuthService).should().authenticateAndUpdate(email, null);
    then(jwtService).shouldHaveNoInteractions();
  }
}
