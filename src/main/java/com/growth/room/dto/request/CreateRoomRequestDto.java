package com.growth.room.dto.request;

import lombok.Builder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Builder
public record CreateRoomRequestDto(
  @NotBlank(message = "숙소 이름은 필수 값입니다.")
  String title,

  String description,

  @NotBlank(message = "주소는 필수 값입니다.")
  String address,

  @NotNull(message = "가격은 필수 값입니다.")
  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  Integer price,

  @NotNull(message = "최대 인원수는 필수 값입니다.")
  @Min(value = 1, message = "최대 인원수는 1 이상이어야 합니다.")
  Integer maxGuest
) {}

