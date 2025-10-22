package com.growth.member.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.repository.MemberRepository;
import com.growth.support.IntegrationTestBase;
import com.growth.member.domain.Member;
import com.growth.member.dto.request.SignUpMemberRequestDto;
import com.growth.member.dto.response.SignUpMemberResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SignUpMemberService 통합 테스트")
class SignUpMemberServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SignUpMemberService signUpMemberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 기능을 통해 회원을 생성할 수 있다")
    void signUp_Success() {
        // given
        SignUpMemberRequestDto requestDto = new SignUpMemberRequestDto(
                "test@example.com",
                "password123",
                "testuser"
        );

        // when
        SignUpMemberResponseDto response = signUpMemberService.signUp(requestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.nickname()).isEqualTo("testuser");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        Member savedMember = memberRepository.findByEmail("test@example.com").orElse(null);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
        assertThat(savedMember.getNickname()).isEqualTo("testuser");
        // 비밀번호는 암호화되어 저장되어야 함
        assertThat(savedMember.getPassword()).isNotEqualTo("password123");
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외가 발생한다")
    void signUp_DuplicateEmail_ThrowsException() {

        // given
        memberRepository.save(Member.builder()
                .email("duplicate@example.com")
                .password("encryptedPassword")
                .nickname("existing")
                .build());

        SignUpMemberRequestDto requestDto = new SignUpMemberRequestDto(
                "duplicate@example.com",
                "password123",
                "newuser"
        );

        // when & then
        assertThatThrownBy(() -> signUpMemberService.signUp(requestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 존재하는 이메일입니다");
    }
}