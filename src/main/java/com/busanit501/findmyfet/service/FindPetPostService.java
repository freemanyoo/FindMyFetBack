package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.post.FindPetSearchCriteria;
import com.busanit501.findmyfet.repository.PostRepository;
import com.busanit501.findmyfet.repository.PostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FindPetPostService {

    private final PostRepository postRepository;

    /**
     * 검색 조건에 따라 분실 신고 게시글을 검색합니다.
     */
    public Page<Post> searchFindPetPosts(FindPetSearchCriteria criteria) {
        // 날짜 범위 유효성 검증
        if (!criteria.isDateTimeRangeValid()) {
            throw new IllegalArgumentException("분실 시작 시간은 종료 시간보다 이전이어야 합니다.");
        }

        // 나이 범위 유효성 검증
        if (!criteria.isAgeRangeValid()) {
            throw new IllegalArgumentException("최소 나이는 최대 나이보다 작거나 같아야 합니다.");
        }

        // Specification 생성
        Specification<Post> spec = PostSpecification.withCriteria(criteria);

        // 정렬 설정
        Sort sort = createSort(criteria.getSortBy(), criteria.getSortDir());

        // 페이지 설정
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // 검색 실행
        return postRepository.findAll(spec, pageable);
    }

    /**
     * 모든 동물 카테고리 목록을 조회합니다.
     */
    public List<String> getAllAnimalCategories() {
        return postRepository.findDistinctAnimalCategories();
    }

    /**
     * 모든 게시글 타입 목록을 조회합니다.
     */
    public List<Map<String, String>> getAllPostTypes() {
        List<Map<String, String>> postTypes = new ArrayList<>();

        for (Post.PostType type : Post.PostType.values()) {
            Map<String, String> postType = new HashMap<>();
            postType.put("value", type.name());
            postType.put("label", getPostTypeLabel(type));
            postTypes.add(postType);
        }

        return postTypes;
    }

    /**
     * 모든 지역 목록을 조회합니다.
     */
    public List<String> getAllLocations() {
        return postRepository.findDistinctLocations();
    }

    /**
     * 특정 동물 카테고리의 품종 목록을 조회합니다.
     */
    public List<String> getBreedsByAnimalCategory(String animalCategory) {
        if (animalCategory == null || animalCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("동물 카테고리 정보가 필요합니다.");
        }

        return postRepository.findDistinctBreedsByAnimalCategory(animalCategory);
    }

    /**
     * 게시글 ID로 특정 게시글을 조회합니다.
     */
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 게시글을 완료 상태로 변경합니다.
     */
    @Transactional
    public Post completePost(Long id) {
        Post post = findById(id);
        post.complete(); // Post 엔티티의 비즈니스 로직 메서드 사용
        return postRepository.save(post);
    }

    /**
     * 정렬 조건을 생성합니다.
     */
    private Sort createSort(String sortBy, String sortDir) {
        // 허용되는 정렬 필드 검증
        Set<String> allowedSortFields = Set.of("createdAt", "updatedAt", "lostTime", "title", "animalName");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt"; // 기본값으로 설정
        }

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }

    /**
     * PostType에 대한 한국어 라벨을 반환합니다.
     */
    private String getPostTypeLabel(Post.PostType postType) {
        switch (postType) {
            case MISSING:
                return "실종신고";
            case SHELTER:
                return "보호소";
            default:
                return postType.name();
        }
    }
}