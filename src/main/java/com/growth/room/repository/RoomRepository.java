package com.growth.room.repository;

import com.growth.room.domain.Room;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Room 엔티티의 JPA Repository입니다.
 * JpaRepository의 기본 CRUD 기능과 간단한 쿼리 메서드를 제공합니다.
 * 복잡한 동적 쿼리는 RoomRepositoryCustom을 통해 처리됩니다.
 */
public interface RoomRepository extends JpaRepository<Room, UUID>, RoomRepositoryCustom {
  /**
   * 방 ID로 방을 조회합니다.
   * 
   * @param roomId 방 ID
   * @return 해당 방 ID에 해당하는 방 (Optional)
   */
  Optional<Room> findByRoomId(UUID roomId);

  /**
   * 호스트 ID로 방 목록을 조회합니다.
   * JPA 네이밍 규칙을 사용하여 자동으로 쿼리를 생성합니다.
   * 
   * @param hostId 호스트 ID
   * @return 해당 호스트가 등록한 방 목록
   */
  List<Room> findByHostId(UUID hostId);

  /**
   * 방 ID로 방 존재 여부를 확인합니다.
   * 
   * @param roomId 방 ID
   * @return 방이 존재하면 true, 아니면 false
   */
  boolean existsByRoomId(UUID roomId);
}

