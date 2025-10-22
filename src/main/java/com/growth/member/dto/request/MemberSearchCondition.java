package com.growth.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSearchCondition {

    private String email;
    private String nickname;
    private String startDate; // yyyy-MM-dd 형식
    private String endDate;   // yyyy-MM-dd 형식

    @Builder
    public MemberSearchCondition(String email, String nickname, String startDate, String endDate) {
        this.email = email;
        this.nickname = nickname;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}