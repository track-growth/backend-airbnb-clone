package com.growth.room.domain;

import com.growth.global.common.entity.BaseEntity;
import com.growth.member.domain.Member;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id", nullable = false)
  private Member host;

  public static Room from(CreateRoomRequestDto requestDto, Member host) {
    return Room
      .builder()
      .title(requestDto.title())
      .description(requestDto.description())
      .address(requestDto.address())
      .price(requestDto.price())
      .maxGuest(requestDto.maxGuest())
      .host(host)
      .build();
  }

  @Builder
  private Room(
    String title,
    String description,
    String address,
    Integer price,
    Integer maxGuest,
    Member host
  ) {
    this.title = title;
    this.description = description;
    this.address = address;
    this.price = price;
    this.maxGuest = maxGuest;
    this.host = host;
  }
}

