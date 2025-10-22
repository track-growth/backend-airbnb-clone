package com.growth.member.service;

import com.growth.global.exception.BadRequestException;
import com.growth.member.domain.Member;
import com.growth.member.dto.request.SignUpMemberRequestDto;
import com.growth.member.dto.response.SignUpMemberResponseDto;
import com.growth.member.repository.MemberRepository;
import com.growth.member.usecase.SignUpMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SignUpMemberService implements SignUpMemberUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpMemberResponseDto signUp(SignUpMemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.email())) {
            throw new BadRequestException("이미 존재하는 이메일입니다");
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(requestDto.password());

        // Member 엔티티 생성 및 저장
        Member member = Member.from(requestDto, encryptedPassword);
        Member savedMember = memberRepository.save(member);

        // 응답 DTO 변환 후 반환
        return SignUpMemberResponseDto.from(savedMember);
    }
}
