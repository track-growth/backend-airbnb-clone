package com.growth.global.config;

// import com.growth.auth.annotation.RoleIdArgumentResolver;
// import com.growth.auth.annotation.UserIdArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  // TODO: ArgumentResolver 구현
  // private final UserIdArgumentResolver userIdArgumentResolver;
  // private final RoleIdArgumentResolver roleIdArgumentResolver;

  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry
      .addMapping("/**")
      .allowedOrigins(
        "http://localhost:3000",
        "http://localhost:3001",
        "http://localhost:5173",
        "https://www.somemore.site/",
        "https://api.somemore.site/"
      )
      .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
      .allowedHeaders("*")
      .exposedHeaders("Authorization")
      .allowCredentials(true)
      .maxAge(3600);
  }
  // TODO: ArgumentResolver 구현
  // @Override
  // public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
  //     resolvers.add(userIdArgumentResolver);
  //     resolvers.add(roleIdArgumentResolver);
  // }
}
