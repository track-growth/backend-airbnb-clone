package com.growth.auth.jwt.usecase;

import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtUseCase {
  EncodedToken generateToken(UserIdentity userIdentity, TokenType tokenType);

  void validateAccessToken(EncodedToken accessToken, HttpServletResponse response);

  Claims getClaims(EncodedToken accessToken);

  // TODO: Refresh Token 추가
}

