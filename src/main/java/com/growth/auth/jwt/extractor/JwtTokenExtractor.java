package com.growth.auth.jwt.extractor;

import com.growth.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * HTTP 요청에서 JWT 토큰을 추출하는 유틸리티 클래스
 * Authorization 헤더와 쿠키 모두에서 토큰 추출을 지원합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  
  private final CookieUtil cookieUtil;

  /**
   * HTTP 요청에서 JWT 토큰을 추출
   * 
   * @param request HTTP 요청 객체
   * @return 추출된 JWT 토큰 (없으면 null)
   */
  public String extractToken(HttpServletRequest request) {
    // 1. Authorization 헤더에서 토큰 추출 시도
    String token = extractFromHeader(request);
    if (StringUtils.hasText(token)) {
      return token;
    }
    
    // 2. 쿠키에서 Access Token 추출 시도
    return cookieUtil.getAccessTokenFromCookie(request).orElse(null);
  }

  /**
   * Authorization 헤더에서 Bearer 토큰 추출
   * 
   * @param request HTTP 요청 객체
   * @return Bearer 토큰 (없으면 null)
   */
  public String extractFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}
