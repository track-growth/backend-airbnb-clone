/**
 * NOTE: 테스트 환경에서 사용하는 Clock Bean 설정
 * - 고정된 시각을 사용하여 테스트의 일관성 보장
 * - @Profile("test")로 테스트 환경에서만 활성화
 */

package com.growth.global.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestTimeConfig {

  @Bean
  public Clock clock() {
    // NOTE: 테스트에서 사용할 고정 시각 (2025-11-26 12:00:00 UTC)
    return Clock.fixed(Instant.parse("2025-11-26T12:00:00Z"), ZoneId.of("UTC"));
  }
}
