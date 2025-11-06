package com.growth.auth.jwt.domain;

import lombok.Value;

@Value
public class EncodedToken {
  String value;

  public static EncodedToken from(String token) {
    return new EncodedToken(token);
  }
}

