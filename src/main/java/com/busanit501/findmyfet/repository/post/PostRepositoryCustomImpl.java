package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.Post;

import com.busanit501.findmyfet.domain.post.QPost;
import com.busanit501.findmyfet.dto.paging.PageRequestDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Post> search(PageRequestDto pageRequestDTO, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 검색 조건 추가
        if (StringUtils.hasText(pageRequestDTO.getType())) {
            booleanBuilder.and(post.postType.stringValue().eq(pageRequestDTO.getType().toUpperCase()));
        }
        if (StringUtils.hasText(pageRequestDTO.getCategory())) {
            booleanBuilder.and(post.animalCategory.eq(pageRequestDTO.getCategory()));
        }

        // =================== 성별 검색 조건 추가 ===================
        if (StringUtils.hasText(pageRequestDTO.getGender())) {
            booleanBuilder.and(post.gender.stringValue().equalsIgnoreCase(pageRequestDTO.getGender()));
        }

        // ===================  핵심 변경 사항  ===================
        // 기존 : booleanBuilder.and(post.user.address.contains(pageRequestDTO.getRegion()));
        // 변경 : Post 엔티티의 location 필드와 정확히 일치하는 것을 검색합니다.
        if (StringUtils.hasText(pageRequestDTO.getRegion())) {
            booleanBuilder.and(post.location.eq(pageRequestDTO.getRegion()));
        }
        // ==============================================================

        if (StringUtils.hasText(pageRequestDTO.getKeyword())) {
            booleanBuilder.and(post.title.contains(pageRequestDTO.getKeyword())
                    .or(post.content.contains(pageRequestDTO.getKeyword())));
        }

        // 쿼리 생성
        // N+1 문제 방지를 위해 fetchJoin() 추가
        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin() // 작성자 정보를 함께 조회
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc());

        List<Post> content = query.fetch();

        // 전체 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(post.count())
                .from(post)
                .where(booleanBuilder);

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
//post.user.address.contains(...)를 post.location.eq(...)로 변경하여,
// 사용자가 게시글을 작성할 때 직접 입력한 location 값("서울시", "부산시" 등)과 검색어가
// 정확히 일치하는 게시글만 찾도록 수정했습니다.
// 추가로, 게시글 목록 조회 시 각 게시글마다 작성자 정보를 가져오기 위해
// 발생하는 N+1 쿼리 문제를 예방하고자 .leftJoin(post.user).fetchJoin() 코드를
// 추가하여 성능을 최적화했습니다.