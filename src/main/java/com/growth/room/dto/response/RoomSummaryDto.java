package com.growth.room.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.UUID;

/**
 * 방 목록 조회 시 사용되는 요약 정보 DTO입니다.
 * 전체 Room 엔티티 대신 필요한 필드만 조회하여 성능을 최적화합니다.
 * QueryDSL의 Projection 기능을 활용합니다.
 */
@Getter
public class RoomSummaryDto {
  private final UUID roomId;
  private final String title;
  private final Integer price;
  private final String address;
  private final Integer maxGuest;

  /**
   * QueryDSL Projection 전용 생성자입니다.
   * @QueryProjection 어노테이션을 통해 QueryDSL이 자동으로 이 생성자를 사용합니다.
   * 
   * @param roomId 방 ID
   * @param title 방 제목
   * @param price 가격
   * @param address 주소
   * @param maxGuest 최대 인원
   */
  @QueryProjection
  public RoomSummaryDto(UUID roomId, String title, Integer price, String address, Integer maxGuest) {
    this.roomId = roomId;
    this.title = title;
    this.price = price;
    this.address = address;
    this.maxGuest = maxGuest;
  }
}

