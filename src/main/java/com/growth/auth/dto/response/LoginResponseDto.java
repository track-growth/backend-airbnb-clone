/** 
 * @description 로그인 응답 DTO
 */

package com.growth.auth.dto.response;

import com.growth.member.domain.Member;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginResponseDto(
    String email,
    String nickname,
    LocalDateTime lastLoginAt
    ) {
        public static LoginResponseDto from(Member member) {
            return LoginResponseDto.builder()
                // NOTE: Response Body에서 Token 제거
                .email(member.getEmail())
                .nickname(member.getNickname())
                .lastLoginAt(member.getLastLoginAt())
                .build();
    }
}

