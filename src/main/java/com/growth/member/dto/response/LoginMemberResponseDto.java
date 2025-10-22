package com.growth.member.dto.response;

import com.growth.member.domain.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginMemberResponseDto(
        String email,
        String nickname,
        LocalDateTime lastLoginAt
) {
    public static LoginMemberResponseDto from(Member member) {
        return LoginMemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .lastLoginAt(member.getUpdatedAt()) // NOTE: 마지막 업데이트 시간을 로그인 시간으로 사용
                .build();
    }
}
