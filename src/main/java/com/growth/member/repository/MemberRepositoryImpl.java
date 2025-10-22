package com.growth.member.repository;

import com.growth.member.domain.Member;
import com.growth.member.dto.request.MemberSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.growth.member.domain.QMember.member;
import static org.springframework.util.StringUtils.hasText;

/**
 * Member 도메인의 QueryDSL 구현체입니다.
 * 복잡한 동적 쿼리와 페이징 처리를 담당합니다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색 조건에 따른 회원 목록을 조회합니다.
     * 동적 쿼리를 통해 null인 조건은 자동으로 제외됩니다.
     *
     * @param condition 검색 조건 (이메일, 닉네임, 생성일 범위)
     * @return 조건에 맞는 회원 목록 (생성일 내림차순 정렬)
     */
    @Override
    public List<Member> findByCondition(MemberSearchCondition condition) {
        return queryFactory
                .selectFrom(member)
                .where(
                        emailContains(condition.getEmail()),
                        nicknameContains(condition.getNickname()),
                        createdAtBetween(condition.getStartDate(), condition.getEndDate())
                )
                .orderBy(member.createdAt.desc())
                .fetch();
    }

    /**
     * 검색 조건에 따른 회원 목록을 페이징하여 조회합니다.
     * 페이징 정보와 정렬 조건을 함께 처리합니다.
     *
     * @param condition 검색 조건 (이메일, 닉네임, 생성일 범위)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 회원 목록
     */
    @Override
    public Page<Member> searchMembers(MemberSearchCondition condition, Pageable pageable) {
        // 페이징된 데이터를 조회합니다
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(
                        emailContains(condition.getEmail()),
                        nicknameContains(condition.getNickname()),
                        createdAtBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        // 전체 개수를 조회합니다
        long total = Optional.ofNullable(
                queryFactory
                        .select(member.count())
                        .from(member)
                        .where(
                                emailContains(condition.getEmail()),
                                nicknameContains(condition.getNickname()),
                                createdAtBetween(condition.getStartDate(), condition.getEndDate())
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 이메일 포함 조건을 생성합니다.
     * 이메일이 null이거나 빈 문자열이면 null을 반환하여 조건에서 제외됩니다.
     *
     * @param email 검색할 이메일
     * @return 이메일 포함 조건 또는 null
     */
    private BooleanExpression emailContains(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }

    /**
     * 닉네임 포함 조건을 생성합니다.
     * 닉네임이 null이거나 빈 문자열이면 null을 반환하여 조건에서 제외됩니다.
     *
     * @param nickname 검색할 닉네임
     * @return 닉네임 포함 조건 또는 null
     */
    private BooleanExpression nicknameContains(String nickname) {
        return hasText(nickname) ? member.nickname.contains(nickname) : null;
    }

    /**
     * 생성일 범위 조건을 생성합니다.
     * 시작일과 종료일이 모두 있을 때만 조건을 반환합니다.
     *
     * @param startDate 시작일 (yyyy-MM-dd 형식)
     * @param endDate 종료일 (yyyy-MM-dd 형식)
     * @return 생성일 범위 조건 또는 null
     */
    private BooleanExpression createdAtBetween(String startDate, String endDate) {
        if (!hasText(startDate) || !hasText(endDate)) {
            return null;
        }

        // 시작일은 00:00:00, 종료일은 23:59:59로 설정합니다
        LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");

        return member.createdAt.between(start, end);
    }

    /**
     * Spring Data의 Sort를 QueryDSL의 OrderSpecifier로 변환합니다.
     * 지원하는 정렬 필드: email, nickname, createdAt
     * 정렬 조건이 없으면 생성일 내림차순으로 기본 정렬됩니다.
     *
     * @param sort Spring Data Sort 객체
     * @return QueryDSL OrderSpecifier 배열
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // Sort의 각 정렬 조건을 OrderSpecifier로 변환합니다
        sort.forEach(order -> {
            com.querydsl.core.types.Order direction = order.isAscending()
                    ? com.querydsl.core.types.Order.ASC
                    : com.querydsl.core.types.Order.DESC;

            // 필드명에 따라 적절한 OrderSpecifier를 생성합니다
            switch (order.getProperty()) {
                case "email" -> orders.add(new OrderSpecifier<>(direction, member.email));
                case "nickname" -> orders.add(new OrderSpecifier<>(direction, member.nickname));
                case "createdAt" -> orders.add(new OrderSpecifier<>(direction, member.createdAt));
                default -> orders.add(new OrderSpecifier<>(direction, member.createdAt));
            }
        });

        // 정렬 조건이 없으면 생성일 내림차순을 기본으로 설정합니다
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, member.createdAt));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}