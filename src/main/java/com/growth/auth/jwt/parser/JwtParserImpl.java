/** 
 * @description JwtParser 인터페이스를 구현하는 class
 */

package com.growth.auth.jwt.parser;

import com.growth.auth.jwt.domain.EncodedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtParserImpl implements JwtParser {

  @Value("${jwt.secret}")
  private String secret;

  @Override
  public Claims parseToken(EncodedToken token) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token.getValue())
        .getPayload();
  }
}

