package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.FindPetPost;
import com.busanit501.findmyfet.dto.FindPetSearchCriteria;
import com.busanit501.findmyfet.repository.FindPetPostRepository;
import com.busanit501.findmyfet.repository.FindPetPostSpecification;
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

    private final FindPetPostRepository findPetPostRepository;

    /**
     * 검색 조건에 따라 분실 신고 게시글을 검색합니다.
     */
    public Page<FindPetPost> searchFindPetPosts(FindPetSearchCriteria criteria) {
        // 날짜 범위 유효성 검증
        if (!criteria.isDateRangeValid()) {
            throw new IllegalArgumentException("분실 시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        // Specification 생성
        Specification<FindPetPost> spec = FindPetPostSpecification.withCriteria(criteria);

        // 정렬 설정
        Sort sort = createSort(criteria.getSortBy(), criteria.getSortDir());

        // 페이지 설정
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // 검색 실행
        return findPetPostRepository.findAll(spec, pageable);
    }

    /**
     * 모든 동물 타입 목록을 조회합니다.
     */
    public List<Map<String, String>> getAllAnimalTypes() {
        List<Map<String, String>> animalTypes = new ArrayList<>();

        for (FindPetPost.AnimalType type : FindPetPost.AnimalType.values()) {
            Map<String, String> animalType = new HashMap<>();
            animalType.put("value", type.name());
            animalType.put("label", type.getKorean());
            animalTypes.add(animalType);
        }

        return animalTypes;
    }

    /**
     * 모든 성별 목록을 조회합니다.
     */
    public List<Map<String, String>> getAllGenders() {
        List<Map<String, String>> genders = new ArrayList<>();

        for (FindPetPost.Gender gender : FindPetPost.Gender.values()) {
            Map<String, String> genderMap = new HashMap<>();
            genderMap.put("value", gender.name());
            genderMap.put("label", gender.getKorean());
            genders.add(genderMap);
        }

        return genders;
    }

    /**
     * 모든 시·도 목록을 조회합니다.
     */
    public List<String> getAllCityProvinces() {
        return findPetPostRepository.findDistinctCityProvinces();
    }

    /**
     * 특정 시·도의 군/구 목록을 조회합니다.
     */
    public List<String> getDistrictsByCity(String cityProvince) {
        if (cityProvince == null || cityProvince.trim().isEmpty()) {
            throw new IllegalArgumentException("시·도 정보가 필요합니다.");
        }

        return findPetPostRepository.findDistinctDistrictsByCityProvince(cityProvince);
    }

    /**
     * 특정 동물 타입의 품종 목록을 조회합니다.
     */
    public List<String> getBreedsByAnimalType(FindPetPost.AnimalType animalType) {
        if (animalType == null) {
            throw new IllegalArgumentException("동물 타입 정보가 필요합니다.");
        }

        return findPetPostRepository.findDistinctBreedsByAnimalType(animalType);
    }

    /**
     * 게시글 ID로 특정 게시글을 조회합니다.
     */
    public FindPetPost findById(Long id) {
        return findPetPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 게시글의 검색 완료 상태를 토글합니다.
     */
    @Transactional
    public FindPetPost toggleFoundStatus(Long id) {
        FindPetPost post = findById(id);

        // 현재 상태의 반대로 설정
        post.setIsFound(!post.getIsFound());

        return findPetPostRepository.save(post);
    }

    /**
     * 정렬 조건을 생성합니다.
     */
    private Sort createSort(String sortBy, String sortDir) {
        // 허용되는 정렬 필드 검증
        Set<String> allowedSortFields = Set.of("createdAt", "updatedAt", "lostDate", "title", "author");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt"; // 기본값으로 설정
        }

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }
}