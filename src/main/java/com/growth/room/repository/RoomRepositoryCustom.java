package com.growth.room.repository;

import com.growth.room.domain.Room;
import com.growth.room.dto.response.RoomSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Room 도메인의 QueryDSL Custom Repository 인터페이스입니다.
 * 복잡한 동적 쿼리와 최적화된 조회 기능을 제공합니다.
 */
public interface RoomRepositoryCustom {
  /**
   * 호스트 ID로 방 요약 정보 목록을 조회합니다.
   * 전체 엔티티 대신 필요한 필드만 조회하여 성능을 최적화합니다.
   * 
   * @param hostId 호스트 ID
   * @return 방 요약 정보 목록 (roomId, title, price, address, maxGuest)
   */
  List<RoomSummaryDto> findRoomSummariesByHostId(UUID hostId);

  /**
   * 가격 범위와 주소로 방을 검색합니다.
   * 조건이 null이면 해당 조건은 무시됩니다 (동적 쿼리).
   * 
   * @param minPrice 최소 가격 (nullable)
   * @param maxPrice 최대 가격 (nullable)
   * @param address 주소 검색어 (nullable)
   * @return 조건에 맞는 방 목록
   */
  List<Room> searchRooms(Integer minPrice, Integer maxPrice, String address);

  /**
   * 가격 범위와 주소로 방을 검색하고 페이징 처리합니다.
   * 
   * @param minPrice 최소 가격 (nullable)
   * @param maxPrice 최대 가격 (nullable)
   * @param address 주소 검색어 (nullable)
   * @param pageable 페이징 및 정렬 정보
   * @return 페이징된 방 목록
   */
  Page<Room> searchRooms(Integer minPrice, Integer maxPrice, String address, Pageable pageable);

  // TODO: 향후 확장 - 검색 조건 객체를 사용한 고급 검색
  // List<Room> findByCondition(RoomSearchCondition condition);
  // Page<Room> findByCondition(RoomSearchCondition condition, Pageable pageable);
}
