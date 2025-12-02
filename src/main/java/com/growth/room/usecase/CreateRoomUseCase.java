package com.growth.room.usecase;

import com.growth.room.dto.request.CreateRoomRequestDto;
import com.growth.room.dto.response.CreateRoomResponseDto;
import java.util.UUID;

public interface CreateRoomUseCase {
  CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto, UUID hostId);
}

