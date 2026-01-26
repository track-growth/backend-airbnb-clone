package com.growth.auth.resolver;

import com.growth.auth.annotation.CurrentMemberId;
import com.growth.auth.exception.AuthErrorMessage;
import com.growth.global.exception.UnauthorizedException;
import java.util.UUID;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentMemberId 어노테이션이 붙은 파라미터에 현재 인증된 사용자의 memberId(UUID)를 주입하는 ArgumentResolver
 * 
 *  - SecurityContext에서 현재 인증 정보를 가져옴
 *  - Authentication의 Principal에서 UUID를 추출
 *  - 컨트롤러 메서드 파라미터에 자동으로 주입
 */
@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

  /**
   * 이 리졸버가 처리할 수 있는 파라미터인지 확인
   * 
   * @param parameter 메서드 파라미터
   * @return @CurrentMemberId 어노테이션이 있고 타입이 UUID인 경우 true
   */
  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentMemberId.class)
        && UUID.class.equals(parameter.getParameterType());
  }

  /**
   * 실제로 파라미터 값을 resolve(해결)하는 메서드
   * 
   * @param parameter 메서드 파라미터
   * @param mavContainer ModelAndView 컨테이너
   * @param webRequest 웹 요청
   * @param binderFactory 데이터 바인더 팩토리
   * @return SecurityContext에서 추출한 UUID (memberId)
   * @throws IllegalStateException Principal이 UUID가 아닌 경우
   * @throws UnauthorizedException 인증 정보가 없거나 인증되지 않은 경우
   */
  @Override
  public Object resolveArgument(
      @NonNull MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException(AuthErrorMessage.UNAUTHORIZED);
    }

    Object principal = authentication.getPrincipal();

    if (principal == null) {
      throw new NullPointerException("Principal is null");
    }
    
    if (!(principal instanceof UUID)) {
      throw new IllegalStateException("Principal이 UUID 타입이 아닙니다. 실제 타입: " + principal.getClass().getName());
    }

    return principal;
  }
}
