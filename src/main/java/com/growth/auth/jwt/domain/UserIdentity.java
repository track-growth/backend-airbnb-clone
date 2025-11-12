/** 
 * @description 사용자 인증 정보를 저장하는 class
 */

package com.growth.auth.jwt.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserIdentity {
  UUID memberId;
  String email;

  // NOTE: from() 메서드: UserIdentity 객체 생성
  public static UserIdentity from(UUID memberId, String email) {
    // NOTE: builder() 메서드: UserIdentity 객체 생성
    return UserIdentity.builder()
      // - memberId 값 설정
      .memberId(memberId)
      // - email 값 설정
      .email(email)
      // - build() 메서드: UserIdentity 객체 생성
      .build();
  }
}

