package com.growth.room.service;

import com.growth.member.service.MemberAuthService;
import com.growth.room.domain.Room;
import com.growth.room.dto.request.CreateRoomRequestDto;
import com.growth.room.dto.response.CreateRoomResponseDto;
import com.growth.room.repository.RoomRepository;
import com.growth.room.usecase.CreateRoomUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateRoomService implements CreateRoomUseCase {

  private final RoomRepository roomRepository;
  private final MemberAuthService memberAuthService;

  @Override
  public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto, UUID hostId) {
    // NOTE: 존재하지 않는 회원 ID로 방을 만들면 안되므로 회원 존재 여부 확인 (데이터 무결성 보장)
    // - Member 도메인 서비스에 검증 책임을 위임 (도메인 경계 명확화)
    // - MSA 구조에서는 이 부분을 member service API 호출로 대체 가능
    memberAuthService.validateMemberExists(hostId);

    // NOTE: Room 엔티티 생성 및 저장 (hostId만 사용)
    Room room = Room.from(requestDto, hostId);
    Room savedRoom = roomRepository.save(room);

    // NOTE: 응답 DTO 변환 후 반환
    return CreateRoomResponseDto.from(savedRoom, hostId);
  }
}

