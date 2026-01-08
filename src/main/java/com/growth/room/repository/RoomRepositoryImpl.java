package com.growth.room.repository;

import com.growth.room.domain.Room;
import com.growth.room.dto.response.QRoomSummaryDto;
import com.growth.room.dto.response.RoomSummaryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.growth.room.domain.QRoom.room;
import static org.springframework.util.StringUtils.hasText;

/**
 * Room 도메인의 QueryDSL 구현체입니다.
 * 복잡한 동적 쿼리와 최적화된 조회 기능을 담당합니다.
 */
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 호스트 ID로 방 요약 정보 목록을 조회합니다.
     * description 등 불필요한 필드를 제외하고 필요한 필드만 조회하여 성능을 최적화합니다.
     * 
     * @param hostId 호스트 ID
     * @return 방 요약 정보 목록
     */
    @Override
    public List<RoomSummaryDto> findRoomSummariesByHostId(UUID hostId) {
        return queryFactory
                .select(new QRoomSummaryDto(
                        room.roomId,
                        room.title,
                        room.price,
                        room.address,
                        room.maxGuest
                ))
                .from(room)
                .where(room.hostId.eq(hostId))
                .orderBy(room.createdAt.desc())
                .fetch();
    }

    /**
     * 가격 범위와 주소로 방을 검색합니다.
     * 동적 쿼리를 통해 null인 조건은 자동으로 제외됩니다.
     * 
     * @param minPrice 최소 가격 (nullable)
     * @param maxPrice 최대 가격 (nullable)
     * @param address 주소 검색어 (nullable)
     * @return 조건에 맞는 방 목록
     */
    @Override
    public List<Room> searchRooms(Integer minPrice, Integer maxPrice, String address) {
        return queryFactory
                .selectFrom(room)
                .where(
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        addressContains(address)
                )
                .orderBy(room.createdAt.desc())
                .fetch();
    }

    /**
     * 가격 범위와 주소로 방을 검색하고 페이징 처리합니다.
     * 
     * @param minPrice 최소 가격 (nullable)
     * @param maxPrice 최대 가격 (nullable)
     * @param address 주소 검색어 (nullable)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 방 목록
     */
    @Override
    public Page<Room> searchRooms(Integer minPrice, Integer maxPrice, String address, Pageable pageable) {
        // 페이징된 데이터를 조회합니다
        List<Room> content = queryFactory
                .selectFrom(room)
                .where(
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        addressContains(address)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(room.createdAt.desc())
                .fetch();

        // 전체 개수를 조회합니다
        long total = Optional.ofNullable(
                queryFactory
                        .select(room.count())
                        .from(room)
                        .where(
                                priceGoe(minPrice),
                                priceLoe(maxPrice),
                                addressContains(address)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 최소 가격 조건을 생성합니다.
     * minPrice가 null이면 null을 반환하여 조건에서 제외됩니다.
     * 
     * @param minPrice 최소 가격
     * @return 최소 가격 조건 또는 null
     */
    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? room.price.goe(minPrice) : null;
    }

    /**
     * 최대 가격 조건을 생성합니다.
     * maxPrice가 null이면 null을 반환하여 조건에서 제외됩니다.
     * 
     * @param maxPrice 최대 가격
     * @return 최대 가격 조건 또는 null
     */
    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? room.price.loe(maxPrice) : null;
    }

    /**
     * 주소 포함 조건을 생성합니다.
     * address가 null이거나 빈 문자열이면 null을 반환하여 조건에서 제외됩니다.
     * 
     * @param address 검색할 주소
     * @return 주소 포함 조건 또는 null
     */
    private BooleanExpression addressContains(String address) {
        return hasText(address) ? room.address.contains(address) : null;
    }
}

