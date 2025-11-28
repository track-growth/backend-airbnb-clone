package com.growth.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.growth.auth.dto.request.LoginRequestDto;
import com.growth.auth.dto.response.LoginResponseDto;
import com.growth.auth.dto.response.LoginResultDto;
import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import com.growth.support.IntegrationTestBase;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Clock;

@DisplayName("AuthService 통합 테스트")
@Import(AuthServiceIntegrationTest.TestClockConfig.class)
class AuthServiceIntegrationTest extends IntegrationTestBase {
  // NOTE: @Autowired: Spring Framework에서 의존성 주입을 자동으로 처리하는 어노테이션
  // - 직접 만드는 방식보다 테스트 코드 작성이 편리함 + 객체 간 결합도 낮아짐 + 테스트용 DB 사용
  @Autowired
  // NOTE: 로그인/인증 로직을 처리하는 서비스를 주입받음
  // 테스트에서 authService.login() 호출하여 로그인 기능을 테스트 가능
  private AuthService authService;

  @Autowired
  // NOTE: 회원 정보를 저장/조회하는 리포지토리를 주입받음
  // memberRepository.save(member)로 테스트 데이터 저장 가능
  private MemberRepository memberRepository;

  @Autowired
  // NOTE: 비밀번호 암호화를 처리하는 PasswordEncoder를 주입받음 (spring security 제공 framework)
  // 테스트에서 passwordEncoder.encode(password)로 비밀번호 암호화 테스트 가능
  private PasswordEncoder passwordEncoder;

  @Autowired
  // NOTE: 테스트 시 고정 시각 사용
  private Clock clock;

  /**
   * 테스트용 Clock 설정
   * - 테스트 시 고정 시각을 사용하여 시간 관련 테스트를 안정적으로 수행
   * - 운영 환경에서는 TimeConfig의 Clock.systemDefaultZone()이 사용됨
   * - @Profile("!test")로 인해 TimeConfig의 Clock은 테스트 환경에서 등록되지 않음
   */
  @TestConfiguration
  static class TestClockConfig {
    @Bean
    public Clock clock() {
      // NOTE: 테스트용 고정 시각 설정 (2025-11-26 12:00:00 UTC)
      Instant fixedInstant = Instant.parse("2025-11-26T12:00:00Z");
      ZoneId zoneId = ZoneId.of("UTC");
      return Clock.fixed(fixedInstant, zoneId);
    }
  }

  @Test
  @DisplayName("올바른 이메일과 비밀번호로 로그인할 수 있다")
  void login_Success() {
    // 1. Member 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (로그인 성공 시 응답 DTO 반환)
    // 5. assertThat()로 응답 DTO 검증

    // given
    String email = "test@example.com";
    String password = "password123";
    // NOTE: passwordEncoder로 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(password);

    // NOTE: 테스트용 회원 생성 및 저장
    Member member = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    // NOTE: 테스트용 DB에 회원 데이터 저장
    memberRepository.save(member);

    // NOTE: 로그인 요청 DTO 생성
    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // NOTE: 로그인 기능 테스트
    // when
    LoginResultDto loginResult = authService.login(requestDto);
    LoginResponseDto response = loginResult.loginResponseDto();

    // then
    // NOTE: assertThat(): assertj 라이브러리의 메서드로 객체의 값을 검증하는 메서드
    // - 응답 DTO가 null이 아닌지 검증
    assertThat(response).isNotNull();
    // - 응답 DTO의 email이 올바른지 검증
    assertThat(response.email()).isEqualTo(email);
    // - 응답 DTO의 nickname이 올바른지 검증
    assertThat(response.nickname()).isEqualTo("testuser");
    // NOTE: 토큰은 응답 body에 포함되지 않고 쿠키로만 전달됨
    // - 응답 DTO의 accessToken은 더 이상 포함되지 않음
    // - 응답 DTO의 refreshToken은 더 이상 포함되지 않음
    // NOTE: 로그인 성공 시 마지막 로그인 시간 검증
    // 단위 테스트에서는 updatedAt이 null일 수 있음 (실제 저장되지 않기 때문)
    assertThat(response.lastLoginAt()).isNotNull();
    // NOTE: 로그인 성공 시 마지막 로그인 시간이 고정 시각과 정확히 일치하는지 검증
    // NOTE: Clock.fixed()를 사용하여 테스트 시 고정 시각을 사용하므로, 정확한 시간 비교가 가능함
    LocalDateTime expectedTime = LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    assertThat(response.lastLoginAt()).isEqualTo(expectedTime);
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
  void login_NotExistentEmail_ThrowsException() {
    // 1. LoginRequestDto 생성
    // 2. authService.login() 호출하여 로그인 기능 테스트 (존재하지 않는 이메일로 로그인 시 예외 발생)
    // 3. assertThatThrownBy()로 예외 검증

    // given
    String email = "testNotExist@example.com";
    String password = "test123";

    LoginRequestDto requestDto = new LoginRequestDto(email, password);

    // when & then
    // NOTE: assertThatThrownBy(): assertj 라이브러리의 메서드로 예외를 검증하는 메서드
    assertThatThrownBy(() -> authService.login(requestDto))
     // NOTE: isInstanceOf(): 예외 타입을 검증하는 메서드
      .isInstanceOf(BadRequestException.class)
      // NOTE: hasMessage(): 예외 메시지를 검증하는 메서드
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
  void login_WrongPassword_ThrowsException() {
    // 1. Member 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (잘못된 비밀번호로 로그인 시 예외 발생)
    // 5. assertThatThrownBy()로 예외 검증

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
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");
  }

  @Test
  @DisplayName("여러 회원이 있을 때 특정 회원으로 로그인할 수 있다")
  void login_WithMultipleMembers_Success() {
    // 1. 여러 회원 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 여러 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (여러 회원 중 특정 회원으로 로그인 시 응답 DTO 반환)
    // 5. assertThat()로 응답 DTO 검증

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

    LoginResultDto loginResult = authService.login(requestDto);
    LoginResponseDto response = loginResult.loginResponseDto();

    // then
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo(email1);
    assertThat(response.nickname()).isEqualTo("user1");
  }

  @Test
  @DisplayName("대소문자가 다른 이메일로 로그인 시 예외가 발생한다")
  void login_CaseSensitiveEmail_ThrowsException() {
    // 1. 회원 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (대소문자가 다른 이메일로 로그인 시 예외 발생)
    // 5. assertThatThrownBy()로 예외 검증

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
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("공백이 포함된 이메일로 로그인 시 예외가 발생한다")
  void login_EmailWithWhitespace_ThrowsException() {
    // 1. 회원 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (공백이 포함된 이메일로 로그인 시 예외 발생)
    // 5. assertThatThrownBy()로 예외 검증

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
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("회원 정보가 없습니다.");
  }

  @Test
  @DisplayName("공백이 포함된 비밀번호로 로그인 시 예외가 발생한다")
  void login_PasswordWithWhitespace_ThrowsException() {
    // 1. 회원 엔티티 생성
    // 2. memberRepository.save(member)로 테스트용 DB에 회원 데이터(member 엔티티) 저장
    // 3. LoginRequestDto 생성
    // 4. authService.login() 호출하여 로그인 기능 테스트 (공백이 포함된 비밀번호로 로그인 시 예외 발생)
    // 5. assertThatThrownBy()로 예외 검증

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
    assertThatThrownBy(() -> authService.login(requestDto))
      .isInstanceOf(BadRequestException.class)
      .hasMessage("비밀번호가 일치하지 않습니다.");
  }
}
