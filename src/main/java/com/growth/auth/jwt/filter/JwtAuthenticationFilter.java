package com.growth.auth.jwt.filter;

import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.service.JwtService;
import com.growth.auth.util.CookieUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CookieUtil cookieUtil;
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String token = extractToken(request);

    if (StringUtils.hasText(token)) {
      try {
        EncodedToken encodedToken = EncodedToken.from(token);
        Claims claims = jwtService.getClaims(encodedToken);
        
        // JWT에서 memberId 추출 (subject에 저장됨)
        String memberIdStr = claims.getSubject();
        UUID memberId = UUID.fromString(memberIdStr);

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
          memberId,
          null,
          java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        log.error("JWT 토큰 처리 실패: {}", e.getMessage());
        // 인증 실패 시 SecurityContext를 비우고 계속 진행
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    // 1. Authorization 헤더에서 토큰 추출 시도
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    
    // 2. 쿠키에서 Access Token 추출 시도
    return cookieUtil.getAccessTokenFromCookie(request).orElse(null);
  }
}

