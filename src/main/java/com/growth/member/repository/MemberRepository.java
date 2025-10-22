package com.growth.member.repository;

import com.growth.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Member 엔티티의 JPA Repository입니다.
 * JpaRepository의 기본 CRUD 기능과 간단한 쿼리 메서드를 제공합니다.
 * 복잡한 동적 쿼리는 MemberRepositoryCustom을 통해 처리됩니다.
 */
public interface MemberRepository extends JpaRepository<Member, UUID>, MemberRepositoryCustom {

    /**
     * 이메일로 회원을 조회합니다.
     *
     * @param email 조회할 이메일
     * @return 이메일에 해당하는 회원 (Optional)
     */
    Optional<Member> findByEmail(String email);

    /**
     * 이메일 존재 여부를 확인합니다.
     * 회원가입 시 이메일 중복 검사에 사용됩니다.
     *
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);
}