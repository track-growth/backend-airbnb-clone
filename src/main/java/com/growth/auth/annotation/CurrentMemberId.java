package com.growth.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 인증된 사용자의 memberId(UUID)를 컨트롤러 메서드 파라미터에 자동으로 주입하는 어노테이션
 * 
 * 사용 예시:
 * {@code
 * @PostMapping("/api/rooms")
 * public ApiResponse<CreateRoomResponseDto> createRoom(
 *     @RequestBody CreateRoomRequestDto requestDto,
 *     @CurrentMemberId UUID memberId
 * ) {
 *     // memberId를 바로 사용 가능
 * }
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentMemberId {
  // NOTE: @CurrentMemberId 어노테이션은 Marker 역할만 하고, 실제 로직은 ArgumentResolver에서 처리됨

}
