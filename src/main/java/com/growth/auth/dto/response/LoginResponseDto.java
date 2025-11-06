package com.growth.auth.dto.response;

import com.growth.member.domain.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginResponseDto(
        String accessToken,
        String email,
        String nickname,
        LocalDateTime lastLoginAt
) {
    public static LoginResponseDto from(Member member, String accessToken) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .lastLoginAt(member.getLastLoginAt())
                .build();
    }
}

