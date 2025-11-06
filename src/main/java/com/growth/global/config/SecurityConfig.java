package com.growth.global.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
      // 요청 권한 설정
      .authorizeHttpRequests(
        auth ->
          auth
            // 회원가입, 로그인은 인증 없이 접근 가능
            .requestMatchers(
              "/api/members/signup",
              "/api/members/login",
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

    // Origin 설정
    configuration.setAllowedOrigins(
      Arrays.asList("http://localhost:3000", "http://localhost:3001")
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
