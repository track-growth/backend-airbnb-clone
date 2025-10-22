package com.growth.member.controller;

import com.growth.member.dto.request.SignUpMemberRequestDto;
import com.growth.member.dto.response.SignUpMemberResponseDto;
import com.growth.member.usecase.SignUpMemberUseCase;
import com.growth.support.WebIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("MemberCommandController 테스트")
class MemberCommandControllerTest extends WebIntegrationTestBase {

    private SignUpMemberUseCase signUpMemberUseCase;
    private MemberCommandController memberCommandController;

    @BeforeEach
    void setup() {
        // Mockito Mock 객체 생성
        signUpMemberUseCase = Mockito.mock(SignUpMemberUseCase.class);
        // 컨트롤러 직접 주입
        memberCommandController = new MemberCommandController(signUpMemberUseCase);
        // MockMvc 생성
        setupMockMvc(memberCommandController);
    }

    @Test
    @DisplayName("회원가입 성공 시 201 Created 반환")
    void signUpSuccess() throws Exception {
        SignUpMemberRequestDto request = new SignUpMemberRequestDto(
                "test@example.com",
                "testUser",
                "password123!"
        );

        SignUpMemberResponseDto response = SignUpMemberResponseDto.builder()
                .email("test@example.com")
                .nickname("testUser")
                .createdAt(LocalDateTime.now())
                .build();

        given(signUpMemberUseCase.signUp(any(SignUpMemberRequestDto.class))).willReturn(response);

        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("testUser"));
    }
}
