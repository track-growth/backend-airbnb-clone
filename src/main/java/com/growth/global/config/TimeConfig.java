/**
 * NOTE: 프로덕션 환경에서 사용하는 Clock Bean 설정
 * - 시스템의 현재 시각을 사용
 * - 테스트 환경에서는 TestTimeConfig가 우선 적용됨 (@Profile("test"))
 * 
 * MemberAuthService에서 로그인 시각 기록 등에 사용됨
 */

package com.growth.global.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
public class TimeConfig {

  @Bean
  public Clock clock() {
    // NOTE: 프로덕션에서는 시스템의 현재 시각 사용
    return Clock.systemDefaultZone();
  }
}


