package com.growth.auth.jwt.service;

import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.generator.JwtGenerator;
import com.growth.auth.jwt.parser.JwtParser;
import com.growth.auth.jwt.usecase.JwtUseCase;
import com.growth.auth.jwt.validator.JwtValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements JwtUseCase {

  private final JwtGenerator jwtGenerator;
  private final JwtParser jwtParser;
  private final JwtValidator jwtValidator;


  @Override
  public EncodedToken generateToken(UserIdentity userIdentity, TokenType tokenType) {
    return jwtGenerator.generateToken(userIdentity, tokenType);
  }

  @Override
  public void validateAccessToken(EncodedToken accessToken, HttpServletResponse response) {
    jwtValidator.validateToken(accessToken);
  }

  @Override
  public Claims getClaims(EncodedToken accessToken) {
    return jwtParser.parseToken(accessToken);
  }

  // TODO: Refresh Token 추가
}

