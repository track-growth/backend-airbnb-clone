package com.growth.room.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.repository.MemberRepository;
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
  private final MemberRepository memberRepository;

  @Override
  public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto, UUID hostId) {
    // NOTE: 존재하지 않는 회원 ID로 방을 만들면 안되므로 회원 존재 여부 확인 (데이터 무결성 보장)
    // - MSA 구조에서는 이 부분을 member service API 호출로 대체 가능
    // - 예: memberServiceClient.existsById(hostId)
    if (!memberRepository.existsById(hostId)) {
      throw new BadRequestException("존재하지 않는 회원은 숙소를 생성할 수 없습니다.");
    }

    // NOTE: Room 엔티티 생성 및 저장 (hostId만 사용)
    Room room = Room.from(requestDto, hostId);
    Room savedRoom = roomRepository.save(room);

    // NOTE: 응답 DTO 변환 후 반환
    return CreateRoomResponseDto.from(savedRoom, hostId);
  }
}

