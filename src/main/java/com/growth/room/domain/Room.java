package com.growth.room.domain;

import com.growth.global.common.entity.BaseEntity;
import com.growth.room.dto.request.CreateRoomRequestDto;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "room")
public class Room extends BaseEntity {
  @Id
  @Column(name = "room_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID roomId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "max_guest", nullable = false)
  private Integer maxGuest;

  // NOTE: MSA 구조에서 member service와 room service가 분리될 경우를 대비
  // Member 엔티티를 직접 참조하지 않고 hostId만 저장
  // 회원 정보가 필요한 경우 member service API를 호출하여 조회 가능
  @Column(name = "host_id", nullable = false)
  private UUID hostId;

  public static Room from(CreateRoomRequestDto requestDto, UUID hostId) {
    return Room
      .builder()
      .title(requestDto.title())
      .description(requestDto.description())
      .address(requestDto.address())
      .price(requestDto.price())
      .maxGuest(requestDto.maxGuest())
      .hostId(hostId)
      .build();
  }

  @Builder
  private Room(
    String title,
    String description,
    String address,
    Integer price,
    Integer maxGuest,
    UUID hostId
  ) {
    this.title = title;
    this.description = description;
    this.address = address;
    this.price = price;
    this.maxGuest = maxGuest;
    this.hostId = hostId;
  }
}

