/**
 * NOTE: Clock Bean 관련 설정
 * - 시스템 시간을 사용하여 마지막 로그인 시간 업데이트
 * - 테스트 시 고정 시각 사용
 * - 실제 운영 시 시스템 시간 사용
 * - 테스트 시 고정 시각 사용
 * 📌 Question: 전역에서 사용하는 인프라 설정이므로 해당 global/config 경로에 위치하는 게 맞는지?? 그리고 이렇게 작성하는게 맞는지,,,
 */

package com.growth.global.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TimeConfig {
   // NOTE: 운영 환경에서 사용하는 Clock 빈
   // 📌 Question: override 오류가 나서 이렇게 설정했는데, 이렇게 설정해도 되는지,,?
  @Bean
  // NOTE: 테스트 환경에서는 등록되지 않음 (@Profile("!test"))
  // - 테스트 환경에서는 TestClockConfig의 고정 시각 Clock이 사용됨
  @Profile("!test")
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
