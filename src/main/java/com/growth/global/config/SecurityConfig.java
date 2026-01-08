package com.growth.global.config;

import com.growth.auth.jwt.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      // CSRF 비활성화 (REST API는 stateless하므로)
      .csrf(AbstractHttpConfigurer::disable)
      // CORS 설정 적용 (WebConfig와 동일한 설정)
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      // 세션 사용하지 않음 (JWT 사용하실거라면..?)
      .sessionManagement(
        session ->
          session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      // JWT 필터 추가
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      // 인증 예외 처리: 인증되지 않은 요청에 대해 401 Unauthorized 반환
      .exceptionHandling(
        exception ->
          exception.authenticationEntryPoint(
            (request, response, authException) -> {
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
          )
      )
      // 요청 권한 설정
      .authorizeHttpRequests(
        auth ->
          auth
            // NOTE: 회원가입, 로그인은 인증 없이 접근 가능
            .requestMatchers(
              "/api/members/signup",
              "/api/auth/login"
            )
            .permitAll()
            // Actuator health check 허용 - CD, 모니터링에 필요함
            .requestMatchers("/actuator/health")
            .permitAll()
            // 나머지 요청은 인증 필요
            .anyRequest()
            .authenticated()
      );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // NOTE: Origin 설정 (allowCredentials(true)와 함께 사용하려면 setAllowedOriginPatterns 사용)
    // NOTE: setAllowedOrigins와 setAllowCredentials(true)를 함께 사용하면 오류 발생 가능하여 setAllowedOriginPatterns 사용
    configuration.setAllowedOriginPatterns(
      Arrays.asList("http://localhost:3000")
    );

    // HTTP Method 설정
    configuration.setAllowedMethods(
      Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
    );

    // HTTP Header 설정
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // 노출할 Header 설정
    configuration.setExposedHeaders(Arrays.asList("Authorization"));

    // Credentials 허용 여부
    configuration.setAllowCredentials(true);

    // Preflight 요청 결과 캐시 시간
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
