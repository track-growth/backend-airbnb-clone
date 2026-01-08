package com.growth.auth.jwt.validator;

import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.parser.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

  private final JwtParser jwtParser;

  public void validateToken(EncodedToken token) {
    try {
      jwtParser.parseToken(token);
    } catch (Exception e) {
      log.error("JWT 토큰 검증 실패: {}", e.getMessage());
      throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", e);
    }
  }
}

