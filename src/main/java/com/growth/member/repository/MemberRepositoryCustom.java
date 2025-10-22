package com.growth.member.repository;

import com.growth.member.domain.Member;
import com.growth.member.dto.request.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Member 엔티티의 QueryDSL 기반 Custom Repository 인터페이스입니다.
 * 복잡한 동적 쿼리와 페이징 처리를 위한 메서드를 정의합니다.
 * 구현체는 MemberRepositoryImpl입니다.
 */
public interface MemberRepositoryCustom {

    /**
     * 검색 조건에 따른 회원 목록을 조회합니다.
     * 이메일, 닉네임, 생성일 범위로 동적 검색이 가능합니다.
     *
     * @param condition 검색 조건 (이메일, 닉네임, 생성일 범위)
     * @return 조건에 맞는 회원 목록 (생성일 내림차순 정렬)
     */
    List<Member> findByCondition(MemberSearchCondition condition);

    /**
     * 검색 조건에 따른 회원 목록을 페이징하여 조회합니다.
     * 페이징 정보와 정렬 조건을 함께 적용할 수 있습니다.
     *
     * @param condition 검색 조건 (이메일, 닉네임, 생성일 범위)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 회원 목록
     */
    Page<Member> searchMembers(MemberSearchCondition condition, Pageable pageable);
}