package com.growth.room.controller;

import com.growth.auth.annotation.CurrentMemberId;
import com.growth.global.common.response.ApiResponse;
import com.growth.room.dto.request.CreateRoomRequestDto;
import com.growth.room.dto.response.CreateRoomResponseDto;
import com.growth.room.usecase.CreateRoomUseCase;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomCommandController {
  private final CreateRoomUseCase createRoomUseCase;

  @PostMapping("/api/rooms/create")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<CreateRoomResponseDto> createRoom(
    @Valid @RequestBody CreateRoomRequestDto requestDto,
    @CurrentMemberId UUID hostId
  ) {
    CreateRoomResponseDto response = createRoomUseCase.createRoom(requestDto, hostId);
    return ApiResponse.created(response, "숙소가 생성되었습니다");
  }
}

