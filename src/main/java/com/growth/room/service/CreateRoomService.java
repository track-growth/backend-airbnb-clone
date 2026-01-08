package com.growth.room.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
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
  public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto, UUID memberId) {
    // NOTE: 회원 엔티티 조회
    Member member = memberRepository
      .findById(memberId)
      .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

    // NOTE: Room 엔티티 생성 및 저장
    Room room = Room.from(requestDto, member);
    Room savedRoom = roomRepository.save(room);

    // NOTE: 응답 DTO 변환 후 반환 (이미 로드된 memberId를 사용하여 LAZY 로딩 문제 방지)
    return CreateRoomResponseDto.from(savedRoom, memberId);
  }
}

