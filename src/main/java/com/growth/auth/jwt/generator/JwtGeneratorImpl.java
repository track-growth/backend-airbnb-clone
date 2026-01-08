/** 
 * @description JwtGenerator 인터페이스를 구현하는 class
 */

package com.growth.auth.jwt.generator;

import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;
import com.growth.auth.jwt.domain.UserIdentity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtGeneratorImpl implements JwtGenerator {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Override
  public EncodedToken generateToken(UserIdentity userIdentity, TokenType tokenType) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    
    long expiration = tokenType == TokenType.ACCESS 
        ? accessTokenExpiration 
        : accessTokenExpiration * 7; // Refresh token은 7일 (임시)

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    String token = Jwts.builder()
        .subject(userIdentity.getMemberId().toString())
        .claim("email", userIdentity.getEmail())
        .claim("type", tokenType.name())
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key)
        .compact();

    return EncodedToken.from(token);
  }
}

