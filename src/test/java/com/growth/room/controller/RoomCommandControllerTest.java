package com.growth.room.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.service.JwtService;
import com.growth.member.domain.Member;
import com.growth.member.repository.MemberRepository;
import com.growth.room.dto.request.CreateRoomRequestDto;
import com.growth.support.IntegrationTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("RoomCommandController 통합 테스트")
@Import(RoomCommandControllerTest.TestClockConfig.class)
class RoomCommandControllerTest extends IntegrationTestBase {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private Member testMember;
  private String accessToken;

  /**
   * 테스트용 Clock 설정
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

  @BeforeEach
  void setUp() {
    // MockMvc 설정 (Spring Security 필터 적용)
    this.mockMvc = MockMvcBuilders
      .webAppContextSetup(webApplicationContext)
      .apply(springSecurity())
      .build();

    // 테스트용 회원 생성 및 저장
    String email = "test@example.com";
    String password = "password123";
    String encodedPassword = passwordEncoder.encode(password);

    testMember = Member
      .builder()
      .email(email)
      .password(encodedPassword)
      .nickname("testuser")
      .build();
    memberRepository.save(testMember);

    // JWT 토큰 생성
    UserIdentity userIdentity = UserIdentity.of(testMember.getMemberId(), testMember.getEmail());
    accessToken = jwtService.generateToken(userIdentity, TokenType.ACCESS).getValue();
  }

  @Test
  @DisplayName("로그인한 사용자가 숙소를 생성할 수 있다")
  void createRoom_Success() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(2)
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.statusCode").value(201))
      .andExpect(jsonPath("$.message").value("숙소가 생성되었습니다"))
      .andExpect(jsonPath("$.data.roomId").exists())
      .andExpect(jsonPath("$.data.title").value("서울 강남구 아파트"))
      .andExpect(jsonPath("$.data.description").value("깨끗하고 조용한 아파트입니다"))
      .andExpect(jsonPath("$.data.address").value("서울시 강남구 테헤란로 123"))
      .andExpect(jsonPath("$.data.price").value(100000))
      .andExpect(jsonPath("$.data.maxGuest").value(2))
      .andExpect(jsonPath("$.data.hostId").value(testMember.getMemberId().toString()))
      .andExpect(jsonPath("$.data.createdAt").exists());
  }

  @Test
  @DisplayName("로그인하지 않은 사용자는 숙소를 생성할 수 없다")
  void createRoom_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(2)
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("유효하지 않은 토큰으로는 숙소를 생성할 수 없다")
  void createRoom_WithInvalidToken_ReturnsUnauthorized() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(2)
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer invalidToken")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("숙소 이름이 없는 경우 숙소 생성에 실패한다")
  void createRoom_WithoutTitle_ReturnsBadRequest() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title(null)
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(2)
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("가격이 0보다 작은 경우 숙소 생성에 실패한다")
  void createRoom_WithNegativePrice_ReturnsBadRequest() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(-1000) // 음수 가격
      .maxGuest(2)
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("최대 인원수가 1보다 작은 경우 숙소 생성에 실패한다")
  void createRoom_WithNegativeMaxGuest_ReturnsBadRequest() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(-1) // 음수 최대 인원수
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("최대 인원수가 0인 경우 숙소 생성에 실패한다")
  void createRoom_WithZeroMaxGuest_ReturnsBadRequest() throws Exception {
    // given
    CreateRoomRequestDto request = CreateRoomRequestDto.builder()
      .title("서울 강남구 아파트")
      .description("깨끗하고 조용한 아파트입니다")
      .address("서울시 강남구 테헤란로 123")
      .price(100000)
      .maxGuest(0) // 0 최대 인원수
      .build();

    // when & then
    mockMvc
      .perform(
        post("/api/rooms/create")
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }
}

