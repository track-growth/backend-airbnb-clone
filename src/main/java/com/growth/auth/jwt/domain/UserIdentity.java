package com.growth.auth.jwt.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserIdentity {
  UUID memberId;
  String email;

  public static UserIdentity from(UUID memberId, String email) {
    return UserIdentity.builder()
      .memberId(memberId)
      .email(email)
      .build();
  }
}

