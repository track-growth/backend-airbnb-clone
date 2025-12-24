/** 
 * @description Cookie 관련 유틸 클래스
 */

package com.growth.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  @Value("${jwt.access-token-expiration:86400000}")
  private long accessTokenExpiration;

  @Value("${cookie.http-only:true}")
  private boolean httpOnly;

  @Value("${cookie.secure:false}")
  private boolean secure;

  @Value("${cookie.same-site:Lax}")
  private String sameSite;

  private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;

  /**
   * Access Token을 쿠키에 설정
   */
  public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
    Cookie cookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) (accessTokenExpiration / 1000));
    response.addCookie(cookie);
  }

  /**
   * Refresh Token을 쿠키에 설정
   */
  public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    int maxAge = (int) (accessTokenExpiration * REFRESH_TOKEN_EXPIRATION_DAYS / 1000);
    Cookie cookie = createCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, maxAge);
    response.addCookie(cookie);
  }

  /**
   * Access Token과 Refresh Token을 쿠키에 설정
   * @param response HTTP 응답
   * @param accessToken Access Token
   * @param refreshToken Refresh Token
   */
  public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
    setAccessTokenCookie(response, accessToken);
    setRefreshTokenCookie(response, refreshToken);
  }

  /**
   * Access Token 쿠키 삭제
   */
  public void deleteAccessTokenCookie(HttpServletResponse response) {
    Cookie cookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, "", 0);
    response.addCookie(cookie);
  }

  /**
   * Refresh Token 쿠키 삭제
   */
  public void deleteRefreshTokenCookie(HttpServletResponse response) {
    Cookie cookie = createCookie(REFRESH_TOKEN_COOKIE_NAME, "", 0);
    response.addCookie(cookie);
  }

  /**
   * 요청에서 Access Token 쿠키를 읽어옴
   * @param request HTTP 요청
   * @return Access Token (없으면 Optional.empty())
   */
  public Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
    return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
  }

  /**
   * 요청에서 Refresh Token 쿠키를 읽어옴
   * @param request HTTP 요청
   * @return Refresh Token (없으면 Optional.empty())
   */
  public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
    return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
  }

  /**
   * 요청에서 특정 이름의 쿠키 값을 읽어옴
   * @param request HTTP 요청
   * @param cookieName 쿠키 이름
   * @return 쿠키 값 (없으면 Optional.empty())
   */
  private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return Optional.empty();
    }

    return Arrays.stream(cookies)
      .filter(cookie -> cookieName.equals(cookie.getName()))
      .map(Cookie::getValue)
      .findFirst();
  }

  /**
   * 보안 옵션이 설정된 쿠키 생성
   */
  private Cookie createCookie(String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    // HttpOnly: JavaScript에서 접근 불가 (XSS 공격 방지)
    cookie.setHttpOnly(httpOnly);
    // Secure: HTTPS에서만 전송 (프로덕션 환경에서 true로 설정 권장)
    // 개발 환경에서는 false로 설정 (localhost는 HTTP 사용)
    cookie.setSecure(secure);
    // SameSite: CSRF 공격 방지
    cookie.setAttribute("SameSite", sameSite);
    
    return cookie;
  }
}

