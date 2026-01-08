package com.growth.room.dto.response;

import com.growth.room.domain.Room;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateRoomResponseDto(
  UUID roomId,
  String title,
  String description,
  String address,
  Integer price,
  Integer maxGuest,
  UUID hostId,
  LocalDateTime createdAt
) {
  public static CreateRoomResponseDto from(Room room) {
    return from(room, room.getHostId());
  }

  public static CreateRoomResponseDto from(Room room, UUID hostId) {
    // NOTE: createdAt이 null일 수 있으므로 안전하게 처리
    LocalDateTime createdAt = room.getCreatedAt();
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    
    return CreateRoomResponseDto
      .builder()
      .roomId(room.getRoomId())
      .title(room.getTitle())
      .description(room.getDescription())
      .address(room.getAddress())
      .price(room.getPrice())
      .maxGuest(room.getMaxGuest())
      .hostId(hostId)
      .createdAt(createdAt)
      .build();
  }
}

