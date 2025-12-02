package com.growth.room.repository;

import java.util.List;

import com.growth.room.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// TODO: 검색 조건 추가 시 사용
// import com.growth.room.dto.request.RoomSearchCondition;

public interface RoomRepositoryCoustom {
  /**
   * 검색 조건에 따른 방 목록을 조회합니다.
   * 
   * @param condition 검색 조건
   * @return 조건에 맞는 방 목록
   */
  // TODO: 검색 조건 추가 시 사용
  // List<Room> findByCondition(RoomSearchCondition condition);

  /**
   * 검색 조건에 따른 방 목록을 페이징하여 조회합니다.
   * 
   * @param condition 검색 조건
   * @param pageable 페이징 정보
   * @return 페이징된 방 목록
   */
  // TODO: 검색 조건 추가 시 사용
  // Page<Room> findByCondition(RoomSearchCondition condition, Pageable pageable);
}
